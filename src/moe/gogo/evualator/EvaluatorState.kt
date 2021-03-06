package moe.gogo.evualator

import moe.gogo.Case
import moe.gogo.Compiler
import moe.gogo.Loader
import moe.gogo.check.CheckResult
import moe.gogo.fixer.Mistake
import java.io.File
import java.io.FileNotFoundException
import java.io.PrintStream
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

typealias FileBuilder = (Case) -> File

fun FileBuilder.append(str: String): FileBuilder = { File(this(it).path + str) }

typealias Injector = (case: Case) -> Unit
typealias ErrorHandle = (case: Case, error: Throwable) -> Boolean

class EvaluatorState(val process: QuestionProcess) : Cloneable {

    val user = process.user
    val question = process.question
    val userPath = user.path

    var loader: Loader = user.loader
    var source: File = userPath.resolve("${question.name}.java").toFile()
    var main: Method? = null

    var outputFile: FileBuilder = { userPath.resolve("${it.simpleName}.out").toFile() }
    var args: (Case) -> Array<String> = { it.args }
    var input: (Case) -> String = { it.input }

    var isCompiled = false

    var mistakes = mutableListOf<Mistake>()

    var beforeInvoke: Deque<Injector> = ArrayDeque<Injector>()
    var afterInvoke: Deque<Injector> = ArrayDeque<Injector>()
    var handleError: Deque<ErrorHandle> = ArrayDeque<ErrorHandle>()

    var toClear = mutableListOf<FileBuilder>()

    fun evaluate(): QuestionResult {
        if (!isCompiled) {
            val compileError = compile()
            if (compileError != null) {
                return compileError
            }
            isCompiled = true
        }

        val caseResults = question.cases.map { case ->
            beforeInvoke.forEach { it(case) }
            val result = invoke(case)
            afterInvoke.forEach { it(case) }
            result
        }
        val failResults = caseResults.filter { !it.accept() }

        return if (failResults.isEmpty()) {
            QuestionResult.Accept(process)
        } else {
            QuestionResult.CaseFail(process, failResults)
        }.apply {
            val existFormatError = caseResults.find { it is CaseResult.FormatError } != null
            if (existFormatError) {
                this.mistakes.add(Mistake.FormatError)
            }
        }

    }

    private fun compile(): QuestionResult.CompileError? {
        if (!source.exists()) {
            return QuestionResult.CompileError(
                process,
                "Source File Not Found",
                FileNotFoundException()
            )
        }

        Compiler.compile(source)

        try {
            val c = loader.loadClass(question.name)
            main = c.getMethod("main", Array<String>::class.java)
        } catch (e: NoClassDefFoundError) {
            return QuestionResult.CompileError(process, "Package Included", e)
        } catch (e: Exception) {
            return QuestionResult.CompileError(process, "Load Class Failed", e)
        }
        return null
    }

    private fun invoke(case: Case): CaseResult {
        val invoker = CaseInvoker(
            case,
            outputFile(case),
            args(case),
            input(case)
        )

        return invoker.invoke()
    }

    inner class CaseInvoker(
        val case: Case,
        val output: File,
        val args: Array<String> = case.args,
        val input: String = case.input
    ) {
        val main: Method = this@EvaluatorState.main!!

        fun invoke(): CaseResult {

            redirectInputAndOutput { stdout, stderr ->

                try {
                    invokeMainWithLimitedTime {
                        return CaseResult.TimeLimitExceeded(case)
                    }
                } catch (e: InvocationTargetException) {
                    handleInvocationException(e) { error ->
                        error.printStackTrace()
                        return CaseResult.RuntimeError(case, error)
                    }
                } catch (e: Exception) {
                    e.printStackTrace(stderr)
                    return CaseResult.RuntimeError(case, e)
                }

            }

            return checkResult()

        }

        private inline fun handleInvocationException(
            e: InvocationTargetException,
            uncatchedErrorHandler: (Throwable) -> Unit
        ) {
            val error = e.targetException
            var handled = false
            this@EvaluatorState.handleError.forEach {
                handled = handled || it(case, error)
            }
            if (!handled) {
                uncatchedErrorHandler(error)
            }
        }

        private inline fun redirectInputAndOutput(todo: (PrintStream, PrintStream) -> Unit) {
            val stdout = System.out
            val stderr = System.err

            val output = PrintStream(this.output)
            val input = this.input.byteInputStream()

            System.setIn(input)
            System.setOut(output)
            System.setErr(output)

            try {
                todo(stdout, stderr)
            } finally {
                System.setOut(stdout)
                System.setErr(stderr)
                output.close()
                input.close()
            }
        }

        private inline fun invokeMainWithLimitedTime(timeoutHandler: () -> Unit) {
            var throwable: Throwable? = null
            val thread = Thread {
                main.invoke(null, args)
            }
            thread.setUncaughtExceptionHandler { _, e ->
                throwable = e
            }
            thread.start()
            thread.join(3000)
            if (thread.isAlive) {
                thread.stop()
                Thread.sleep(100)
                timeoutHandler()
            }
            if (throwable != null) {
                throw throwable!!
            }
        }

        private fun checkResult(): CaseResult {
            val result = case.checker.check(case, this.output)

            return when (result) {
                CheckResult.WRONG_ANSWER -> CaseResult.WrongAnswer(case)
                CheckResult.FORMAT_ERROR -> CaseResult.FormatError(case)
                CheckResult.ACCEPT -> CaseResult.Accept(case)
            }
        }

    }

    fun copy(): EvaluatorState {
        val copy = clone() as EvaluatorState
        copy.mistakes = copy.mistakes.toMutableList()
        copy.beforeInvoke = ArrayDeque(copy.beforeInvoke)
        copy.afterInvoke = ArrayDeque(copy.afterInvoke)
        copy.handleError = ArrayDeque(copy.handleError)
        copy.toClear = copy.toClear.toMutableList()
        return copy
    }

    fun requireCompile() {
        isCompiled = false
    }

    fun addMistake(mistake: Mistake) {
        mistakes.add(mistake)
    }

    fun clear() {
        question.cases
            .map { outputFile(it) }
            .forEach { it.delete() }
        question.cases
            .flatMap { case -> toClear.map { it(case) } }
            .forEach { it.deleteRecursively() }
    }
}
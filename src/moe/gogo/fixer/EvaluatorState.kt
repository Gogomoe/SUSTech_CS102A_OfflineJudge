package moe.gogo.fixer

import moe.gogo.Case
import moe.gogo.Compiler
import moe.gogo.Loader
import moe.gogo.evualator.CaseInvoker
import moe.gogo.evualator.CaseResult
import moe.gogo.evualator.QuestionProcess
import moe.gogo.evualator.QuestionResult
import java.io.File
import java.io.FileNotFoundException
import java.lang.reflect.Method
import java.util.*

typealias FileBuilder = (Case) -> File

fun FileBuilder.append(str: String): FileBuilder = { File(this(it).path + str) }

typealias Injector = (case: Case) -> Unit

class EvaluatorState(val process: QuestionProcess) : Cloneable {

    val user = process.user
    val question = process.question
    val userpath = user.path

    var loader: Loader = user.loader
    var source: File = userpath.resolve("${question.name}.java").toFile()
    var main: Method? = null

    var outputFile: FileBuilder = { userpath.resolve("${it.simpleName}.out").toFile() }
    var args: (Case) -> Array<String> = { it.args }
    var inputFile: FileBuilder = { it.input }

    var isCompiled = false

    var mistakes = mutableListOf<Mistake>()

    var beforeInvoke: Deque<Injector> = ArrayDeque<Injector>()
    var afterInvoke: Deque<Injector> = ArrayDeque<Injector>()

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
        val failResults = caseResults.filter { it !is CaseResult.Accept }

        if (failResults.isEmpty()) {
            return QuestionResult.Accept(process)
        }

        return QuestionResult.CaseFail(process, failResults)
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
            main!!,
            outputFile(case),
            args(case),
            inputFile(case)
        )

        return invoker.invoke()
    }

    fun copy(): EvaluatorState {
        val copy = clone() as EvaluatorState
        copy.mistakes = copy.mistakes.toMutableList()
        copy.beforeInvoke = ArrayDeque(copy.beforeInvoke)
        copy.afterInvoke = ArrayDeque(copy.afterInvoke)
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
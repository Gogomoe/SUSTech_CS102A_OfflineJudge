package moe.gogo

import java.io.File
import java.io.PrintStream
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class Process(val user: User, val question: Question, val file: File) {

    var error: String? = null

    fun isError(): Boolean = error != null

    var main: Method? = null

    var wrongAnswerCount = 0

    init {
        if (!file.exists()) {
            error = "Source File Not Found"
        }
    }

    fun evaluate() {

        compile()

        if (!isError()) {
            question.cases.forEach {
                invoke(it)
            }
        }

        if (error == "Wrong Answer") {
            println("\t\u001B[31mFailed Count $wrongAnswerCount/${question.cases.size}\u001B[0m")
        } else if (error == "Input Wrong") {
            println("\u001B[33m$question Input Wrong\u001B[0m")
        } else if (isError()) {
            println("\u001B[31m$question Failed: $error\u001B[0m")
        } else {
            println("\u001B[32m$question Accept\u001B[0m")
        }

    }

    private fun compile() {
        if (isError()) {
            return
        }

        Compiler.compile(file)

        try {
            val c = user.loader.loadClass(question.name)
            main = c.getMethod("main", Array<String>::class.java)
        } catch (e: NoClassDefFoundError) {
            error = "Package Included"
            e.printStackTrace()
        } catch (e: Exception) {
            error = "Load Class Failed"
            e.printStackTrace()
        }

    }

    private fun invoke(case: TestCase) {
        val stdout = System.out
        System.setOut(stdout)

        val outputFile = user.path.resolve("${case.fullName}.out").toFile()

        val invoker = TestInvoker(
            case,
            main!!,
            outputFile
        )
        invoker.invoke()

        if (invoker.runtimeError && invoker.exception is NoSuchElementException) {
            val invoker2 = TestInvoker(
                case,
                main!!,
                outputFile,
                input = case.argsFile
            )
            invoker2.invoke()
            if (invoker2.accept) {
                error = "Input Wrong"
                return
            }
        }

        if (!isError()) {
            error = invoker.error
        }
        if (invoker.wrongAnswer) {
            println("\u001B[31m$question Case ${case.No} Wrong Answer\u001B[0m")
            wrongAnswerCount++
        }

    }

    class TestInvoker(
        val case: TestCase,
        val main: Method,
        val output: File,
        val args: Array<String> = case.args,
        val input: File = case.input
    ) {
        var error: String? = null
        val accept: Boolean
            get() = error == null
        var wrongAnswer = false
        var runtimeError = false
        var exception: Throwable? = null

        fun invoke() {
            val stdout = System.out

            val output = PrintStream(this.output)
            val input = this.input.inputStream()

            System.setIn(input)
            System.setOut(output)
            try {
                main.invoke(null, args)
            } catch (e: InvocationTargetException) {
                exception = e.targetException
                exception!!.printStackTrace(output)
                error = "Runtime Error: ${exception!!.javaClass.name}"
                runtimeError = true
                return
            } catch (e: Exception) {
                error = "Runtime Error"
                e.printStackTrace()
                return
            } finally {
                System.setOut(stdout)
                output.close()
                input.close()
            }

            try {
                compareResult(case, this.output)
            } catch (e: Exception) {
                error = "Wrong Answer"
                wrongAnswer = true
            }

        }

        private fun compareResult(testCase: TestCase, outputFile: File) {
            val res = testCase.answer.lines()
            val out = outputFile.readLines()

            if (res.size != out.size) {
                throw RuntimeException("$this Wrong Answer")
            }
            for (i in 0 until res.size) {
                if (res[i].trim() != out[i].trim()) {
                    throw  RuntimeException("$this Wrong Answer")
                }
            }
        }
    }


}
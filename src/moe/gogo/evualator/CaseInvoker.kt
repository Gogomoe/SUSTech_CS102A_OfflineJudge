package moe.gogo.evualator

import moe.gogo.Case
import moe.gogo.check.CheckResult
import java.io.File
import java.io.PrintStream
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class CaseInvoker(
    val case: Case,
    val main: Method,
    val output: File,
    val args: Array<String> = case.args,
    val input: File = case.input
) {

    fun invoke(): CaseResult {
        val stdout = System.out

        val output = PrintStream(this.output)
        val input = this.input.inputStream()

        System.setIn(input)
        System.setOut(output)
        try {
            main.invoke(null, args)
        } catch (e: InvocationTargetException) {
            val error = e.targetException
            error!!.printStackTrace(output)
            return CaseResult.RuntimeError(case, error)
        } catch (e: Exception) {
            e.printStackTrace()
            return CaseResult.RuntimeError(case, e)
        } finally {
            System.setOut(stdout)
            output.close()
            input.close()
        }

        val result = case.checker.check(case, this.output)
        return if (result == CheckResult.WRONG_ANSWER) {
            CaseResult.WrongAnswer(case)
        } else {
            CaseResult.Accept(case)
        }

    }

}
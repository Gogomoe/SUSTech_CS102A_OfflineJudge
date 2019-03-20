package moe.gogo.evualator

import moe.gogo.*
import java.io.File
import java.io.FileNotFoundException
import java.lang.reflect.Method

class QuestionEvaluator(val user: User, val question: Question, val file: File) {

    var main: Method? = null

    fun evaluate(): QuestionResult {

        if (!file.exists()) {
            return QuestionResult.CompileError(
                this,
                "Source File Not Found",
                FileNotFoundException()
            )
        }

        Compiler.compile(file)

        try {
            val c = user.loader.loadClass(question.name)
            main = c.getMethod("main", Array<String>::class.java)
        } catch (e: NoClassDefFoundError) {
            return QuestionResult.CompileError(this, "Package Included", e)
        } catch (e: Exception) {
            return QuestionResult.CompileError(this, "Load Class Failed", e)
        }

        val caseResults = question.cases.map { invoke(it) }
        val failResults = caseResults.filter { it !is CaseResult.Accept }

        if (failResults.isEmpty()) {
            return QuestionResult.Accept(this)
        }

        return QuestionResult.CaseFail(this, failResults)

    }


    private fun invoke(case: Case): CaseResult {
        val outputFile = user.path.resolve("${case.fullName}.out").toFile()

        val invoker = CaseInvoker(
            case,
            main!!,
            outputFile
        )

        return invoker.invoke()

//        TODO
//        if (result is CaseResult.RuntimeError && result.error is NoSuchElementException) {
//            val invoker2 = CaseInvoker(
//                case,
//                main!!,
//                outputFile,
//                input = case.argsFile
//            )
//            val result2 = invoker2.invoke()
//            if (result2 is CaseResult.Accept) {
//                error = "Input Wrong"
//                return
//            }
//        }


    }

}
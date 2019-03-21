package moe.gogo.evualator

import moe.gogo.Case
import moe.gogo.Compiler
import moe.gogo.Question
import moe.gogo.User
import moe.gogo.fixer.EvaluatorState
import moe.gogo.fixer.InputWrongFixer
import moe.gogo.fixer.MistakeFixer
import java.io.File
import java.io.FileNotFoundException
import java.lang.reflect.Method

class QuestionProcess(val user: User, val question: Question, val source: File) {

    var main: Method? = null

    val fixers: MutableList<MistakeFixer> = mutableListOf()

    init {
        fixers.add(InputWrongFixer())
    }


    fun evaluate(): QuestionResult {

        var state = EvaluatorState(this)
        var result = state.evaluate()

        fixers.forEach {
            if (it.haveMistake(result, state)) {
                val fixed = it.tryToFix(result, state) ?: return@forEach
                state.clear()
                result = fixed.first
                state = fixed.second
            }
        }

        result.mistakes.addAll(state.mistakes)

        return result

//        val compileError = compile()
//        if (compileError != null) {
//            return compileError
//        }
//
//        val caseResults = question.cases.map { invoke(it) }
//        val failResults = caseResults.filter { it !is CaseResult.Accept }
//
//        if (failResults.isEmpty()) {
//            return QuestionResult.Accept(this)
//        }
//
//        return QuestionResult.CaseFail(this, failResults)
    }

    private fun compile(): QuestionResult.CompileError? {
        if (!source.exists()) {
            return QuestionResult.CompileError(
                this,
                "Source File Not Found",
                FileNotFoundException()
            )
        }

        Compiler.compile(source)

        try {
            val c = user.loader.loadClass(question.name)
            main = c.getMethod("main", Array<String>::class.java)
        } catch (e: NoClassDefFoundError) {
            return QuestionResult.CompileError(this, "Package Included", e)
        } catch (e: Exception) {
            return QuestionResult.CompileError(this, "Load Class Failed", e)
        }
        return null
    }

    private fun invoke(case: Case): CaseResult {
        val outputFile = user.path.resolve("${case.fullName}.out").toFile()

        val invoker = CaseInvoker(
            case,
            main!!,
            outputFile
        )

        return invoker.invoke()
    }

}
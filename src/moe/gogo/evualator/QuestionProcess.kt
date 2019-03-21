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

    }

}
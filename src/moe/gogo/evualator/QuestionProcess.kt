package moe.gogo.evualator

import moe.gogo.Question
import moe.gogo.User
import moe.gogo.fixer.*

class QuestionProcess(val user: User, val question: Question) {

    val preFixers: MutableList<PreMistakeFixer> = mutableListOf()
    val fixers: MutableList<MistakeFixer> = mutableListOf()

    init {
        preFixers.add(EncodeWithBomFixer())
        preFixers.add(SystemExitFixer())
        fixers.add(MultiScannerFixer())
        fixers.add(InputWrongFixer())
    }

    fun evaluate(): QuestionResult {

        var state = EvaluatorState(this)

        preFixers.forEach {
            if (it.haveMistake(state)) {
                val fixed = it.tryToFix(state)
                state.clear()
                state = fixed
            }
        }

        var result: QuestionResult = state.evaluate()

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
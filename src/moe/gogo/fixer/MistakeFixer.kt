package moe.gogo.fixer

import moe.gogo.evualator.QuestionResult

abstract class MistakeFixer {

    abstract fun haveMistake(prevResult: QuestionResult, state: EvaluatorState): Boolean

    fun tryToFix(
        prevResult: QuestionResult,
        state: EvaluatorState
    ): Pair<QuestionResult, EvaluatorState>? {
        val fixedState = fix(state.copy())
        val currentResult = fixedState.evaluate()
        if (fixed(prevResult, currentResult)) {
            addMistake(fixedState)
            return currentResult to fixedState
        }
        fixedState.clear()
        return null
    }

    protected abstract fun fix(state: EvaluatorState): EvaluatorState

    protected abstract fun fixed(prevResult: QuestionResult, currentResult: QuestionResult): Boolean

    protected abstract fun addMistake(state: EvaluatorState)

}
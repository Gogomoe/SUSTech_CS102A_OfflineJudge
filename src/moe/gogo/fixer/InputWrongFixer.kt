package moe.gogo.fixer

import moe.gogo.evualator.CaseResult
import moe.gogo.evualator.QuestionResult
import java.util.NoSuchElementException

class InputWrongFixer : MistakeFixer() {

    override fun haveMistake(prevResult: QuestionResult, state: EvaluatorState): Boolean {
        if (prevResult !is QuestionResult.CaseFail) {
            return false
        }
        return countFailedCases(prevResult) > 0
    }

    override fun fix(state: EvaluatorState): EvaluatorState {
        state.inputFile = { it.argsFile }
        return state
    }

    override fun fixed(prevResult: QuestionResult, currentResult: QuestionResult): Boolean {
        if (currentResult !is QuestionResult.CaseFail) {
            return true
        }
        val count = countFailedCases(currentResult)

        prevResult as QuestionResult.CaseFail
        val prevCount = countFailedCases(prevResult)

        return count < prevCount
    }

    private fun countFailedCases(result: QuestionResult.CaseFail): Int {
        return result.failCases
            .filter { it is CaseResult.RuntimeError && it.error is NoSuchElementException }
            .count()
    }

    override fun addMistake(state: EvaluatorState) {
        state.addMistake(Mistake.INPUT_WRONG)
    }

}
package moe.gogo.fixer

import moe.gogo.evualator.EvaluatorState
import moe.gogo.evualator.append

abstract class PreMistakeFixer {

    abstract fun haveMistake(state: EvaluatorState): Boolean

    fun tryToFix(state: EvaluatorState): EvaluatorState {
        val fixedState = fix(state.copy())
        fixedState.outputFile =
            fixedState.outputFile.append("_" + this::class.simpleName!!.removeSuffix("Fixer"))
        addMistake(fixedState)
        return fixedState
    }

    protected abstract fun fix(state: EvaluatorState): EvaluatorState

    protected abstract fun addMistake(state: EvaluatorState)

}
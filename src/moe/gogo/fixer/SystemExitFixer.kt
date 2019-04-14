package moe.gogo.fixer

import moe.gogo.Loader
import moe.gogo.evualator.EvaluatorState

class SystemExitFixer : PreMistakeFixer() {

    override fun haveMistake(state: EvaluatorState): Boolean {
        if (!state.source.exists()) {
            return false
        }
        return state.source.readText().contains("""System\s*\.\s*exit\s*\(\s*\d\s*\)""".toRegex())
    }

    override fun fix(state: EvaluatorState): EvaluatorState {
        var text = state.source.readText()
        text = text.replace(
            """System\s*\.\s*exit\s*\(\s*\d\s*\)""".toRegex(),
            """throw new RuntimeException("SystemExit")"""
        )

        val root = state.userPath.resolve("SystemExit")
        val newSource = root.resolve(state.source.name).toFile()
        newSource.parentFile.mkdirs()
        newSource.createNewFile()
        newSource.writeText(text)
        val newLoader = Loader(root)

        state.source = newSource
        state.loader = newLoader
        state.handleError.addLast { case, error ->
            error.message == "SystemExit"
        }
        state.toClear.add { root.toFile() }
        state.requireCompile()
        return state
    }

    override fun addMistake(state: EvaluatorState) {
        state.addMistake(Mistake.SystemExit)
    }

}
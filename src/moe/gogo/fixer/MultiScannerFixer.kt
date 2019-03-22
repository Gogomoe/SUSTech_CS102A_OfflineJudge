package moe.gogo.fixer

import moe.gogo.Loader
import moe.gogo.evualator.CaseResult
import moe.gogo.evualator.QuestionResult
import java.util.*

class MultiScannerFixer : MistakeFixer() {

    override fun haveMistake(prevResult: QuestionResult, state: EvaluatorState): Boolean {
        if (prevResult !is QuestionResult.CaseFail) {
            return false
        }
        return countFailedCases(prevResult) > 0
    }

    override fun fix(state: EvaluatorState): EvaluatorState {
        var text = state.source.readText()
        text = text.replace(
            """new\s+Scanner\s*\(\s*System\s*\.\s*in\s*\)""".toRegex(),
            "ScannerPatch.getScanner()"
        )
        text = "import moe.gogo.fixer.ScannerPatch;\n$text"

        val root = state.userpath.resolve("MultiScanner")
        val newSource = root.resolve(state.source.name).toFile()
        newSource.parentFile.mkdirs()
        newSource.createNewFile()
        newSource.writeText(text)
        val newLoader = Loader(root)

        state.source = newSource
        state.loader = newLoader
        state.beforeInvoke.addFirst { case ->
            val scanner = Scanner(state.inputFile(case))
            ScannerPatch.setScanner(scanner)
        }
        state.toClear.add { root.toFile() }
        state.requireCompile()
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

    override fun addMistake(state: EvaluatorState) {
        state.addMistake(Mistake.MULTI_SCANNER)
    }

    private fun countFailedCases(result: QuestionResult.CaseFail): Int {
        return result.failCases
            .filter { it is CaseResult.RuntimeError && it.error is NoSuchElementException }
            .count()
    }

}
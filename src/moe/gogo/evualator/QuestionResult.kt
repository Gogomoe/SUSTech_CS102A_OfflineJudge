package moe.gogo.evualator

import moe.gogo.Console
import moe.gogo.fixer.Mistake
import moe.gogo.fixer.show

sealed class QuestionResult(val process: QuestionProcess) {

    val question = process.question

    val mistakes = mutableListOf<Mistake>()

    abstract fun show()

    class Accept(process: QuestionProcess) : QuestionResult(process) {
        override fun show() {
            Console.accept("$question Accept")
            Console.warning(mistakes.show())
            Console.newline()
        }
    }

    class CaseFail(
        process: QuestionProcess,
        val failCases: List<CaseResult>
    ) : QuestionResult(process) {
        override fun show() {
            failCases.forEach { it.show() }
            Console.error("\t$question Fail Cases: ${failCases.size}/${question.cases.size}")
            Console.warning(mistakes.show())
            Console.newline()
        }
    }

    class CompileError(
        process: QuestionProcess,
        val error: String,
        val exception: Throwable
    ) :
        QuestionResult(process) {
        override fun show() {
            Console.error("$question Failed: $error")
            Console.warning(mistakes.show())
            Console.newline()
        }
    }


}
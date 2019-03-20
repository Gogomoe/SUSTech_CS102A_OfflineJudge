package moe.gogo.evualator

import moe.gogo.Console
import java.lang.Exception

sealed class QuestionResult(val evaluator: QuestionEvaluator) {

    val question = evaluator.question

    abstract fun show()

    class Accept(evaluator: QuestionEvaluator) : QuestionResult(evaluator) {
        override fun show() {
            Console.accept("$question Accept")
        }
    }

    class CaseFail(
        evaluator: QuestionEvaluator,
        val failCases: List<CaseResult>
    ) : QuestionResult(evaluator) {
        override fun show() {
            failCases.forEach { it.show() }
            Console.error("\t$question Fail Cases: ${failCases.size}/${question.cases.size}")
        }
    }

    class CompileError(
        evaluator: QuestionEvaluator,
        val error: String,
        val exception: Throwable
    ) :
        QuestionResult(evaluator) {
        override fun show() {
            Console.error("$question Failed: $error")
        }
    }


}
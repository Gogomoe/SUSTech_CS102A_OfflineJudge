package moe.gogo.evualator

import moe.gogo.Console
import moe.gogo.Case

sealed class CaseResult(val case: Case) {

    abstract fun show()

    class Accept(case: Case) : CaseResult(case) {
        override fun show() {
            Console.accept("${case.fullName} Accept")
        }
    }

    class WrongAnswer(case: Case) : CaseResult(case) {
        override fun show() {
            Console.error("${case.fullName} Wrong Answer")
        }
    }

    class RuntimeError(case: Case, val error: Throwable) : CaseResult(case) {
        override fun show() {
            Console.error("${case.fullName} Runtime Error: ${error.javaClass.name}")
        }
    }

}
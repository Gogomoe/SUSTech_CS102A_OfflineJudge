package moe.gogo.evualator

import moe.gogo.Case
import moe.gogo.Console

sealed class CaseResult(val case: Case) {

    open fun accept() = false

    abstract fun show()

    class Accept(case: Case) : CaseResult(case) {
        override fun accept() = true

        override fun show() {
            Console.accept("${case.fullName} Accept")
            Console.newline()
        }
    }

    class FormatError(case: Case) : CaseResult(case) {
        override fun accept() = true

        override fun show() {
            Console.error("${case.fullName} Format Error")
            Console.newline()
        }
    }

    class WrongAnswer(case: Case) : CaseResult(case) {
        override fun show() {
            Console.error("${case.fullName} Wrong Answer")
            Console.newline()
        }
    }

    class TimeLimitExceeded(case: Case) : CaseResult(case) {
        override fun show() {
            Console.error("${case.fullName} Time Limit Exceeded")
            Console.newline()
        }
    }

    class RuntimeError(case: Case, val error: Throwable) : CaseResult(case) {
        override fun show() {
            Console.error("${case.fullName} Runtime Error: ${error.javaClass.name}")
            Console.newline()
        }
    }

}
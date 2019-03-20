package moe.gogo.check

import moe.gogo.Case
import java.io.File

class CompleteChecker : Checker() {

    override fun check(case: Case, output: File): CheckResult {
        val res = case.answer.lines()
        val out = output.readLines()

        if (res.size != out.size) {
            return CheckResult.WRONG_ANSWER
        }
        for (i in 0 until res.size) {
            if (res[i].trimEnd() != out[i].trimEnd()) {
                return CheckResult.WRONG_ANSWER
            }
        }
        return CheckResult.ACCEPT
    }
}
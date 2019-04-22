package moe.gogo.check

import moe.gogo.Case
import java.io.File

class FormatChecker : Checker() {

    private val completeChecker = CompleteChecker()

    override fun check(case: Case, output: File): CheckResult {
        if (completeChecker.check(case, output) == CheckResult.ACCEPT) {
            return CheckResult.ACCEPT
        }

        val answer = case.answer.split("""\s+""".toRegex())
        val answerChars = case.answer.toCharArray().filter { !it.isWhitespace() }.toSet()
        var text = output.readText()

        answer.forEach {
            val index = text.indexOf(it)
            if (index == -1) {
                return CheckResult.WRONG_ANSWER
            }
            val dropped = text.substring(0, index)
            dropped.forEach { char ->
                if (char in answerChars) {
                    return CheckResult.WRONG_ANSWER
                }
            }

            text = text.drop(index + it.length)
        }
        return CheckResult.FORMAT_ERROR
    }

}
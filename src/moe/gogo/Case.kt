package moe.gogo

import moe.gogo.check.Checker

class Case(
    val question: Question,
    val index: Int,
    val score: Int,
    var input: String,
    var args: Array<String>,
    var answer: String
) {

    val checker: Checker
        get() = question.checker

    val fullName = "$question Case $index"
    val simpleName = "${question}C$index"

}

class CaseBuilder(val index: Int) {

    var input: String = ""
    var args: Array<String> = emptyArray()
    var answer: String = ""

    var score: Int = 0

    var argsString: String
        get() = args.joinToString(" ")
        set(value) {
            args = value.split(spaceRegex).toTypedArray()
        }

    companion object {
        private val spaceRegex = """\s+""".toRegex()
    }

    fun build(question: Question): Case {
        return Case(question, index, score, input, args, answer)
    }

}
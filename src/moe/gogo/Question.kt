package moe.gogo

import moe.gogo.check.Checker
import moe.gogo.check.CompleteChecker

class Question(val assignment: Assignment, val name: String) {

    var cases: List<Case> = mutableListOf()

    var checker: Checker = CompleteChecker()

    override fun toString(): String = name

}

class QuestionBuilder(val name: String) {

    val cases: MutableList<CaseBuilder> = mutableListOf()

    var checker: Checker = CompleteChecker()

    private var index = 1

    fun addCase(builder: CaseBuilder.() -> Unit) {
        val case = CaseBuilder(index++)
        case.builder()
        cases.add(case)
    }

    fun build(assignment: Assignment): Question {
        return Question(assignment, name).also { question ->
            question.checker = checker
            question.cases = cases.map { it.build(question) }
        }
    }

    var score: Int = 0

}

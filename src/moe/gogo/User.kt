package moe.gogo

import moe.gogo.evualator.QuestionEvaluator

class User(val assignment: Assignment, val name: String) {

    val path = assignment.path.resolve(name).resolve("Submission attachment(s)")

    val loader = Loader(path)

    val evaluators: MutableMap<Question, QuestionEvaluator> = mutableMapOf()

    init {
        assignment.questions.forEach {
            val file = path.resolve("${it.name}.java").toFile()
            val evaluator = QuestionEvaluator(this, it, file)
            evaluators[it] = evaluator
        }
    }

    override fun toString(): String = name


}
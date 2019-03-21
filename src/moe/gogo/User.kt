package moe.gogo

import moe.gogo.evualator.QuestionProcess

class User(val assignment: Assignment, val name: String) {

    val path = assignment.path.resolve(name).resolve("Submission attachment(s)")

    val loader = Loader(path)

    val processes: MutableMap<Question, QuestionProcess> = mutableMapOf()

    init {
        assignment.questions.forEach {
            val file = path.resolve("${it.name}.java").toFile()
            val evaluator = QuestionProcess(this, it, file)
            processes[it] = evaluator
        }
    }

    override fun toString(): String = name


}
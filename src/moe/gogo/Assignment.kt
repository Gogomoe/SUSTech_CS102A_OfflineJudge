package moe.gogo

import moe.gogo.evualator.QuestionProcess
import java.nio.file.Path

class Assignment(val name: String, val path: Path) {

    var questions: List<Question> = emptyList()

    var users: List<User> = emptyList()

    fun evaluate() {
        for (user in users) {
            Console.println(user)
            user.evaluate()
        }
    }

    private fun User.evaluate() {
        for (question in questions) {
            val process = QuestionProcess(this, question)
            val result = process.evaluate()
            result.show()
        }
    }

    companion object {
        operator fun invoke(
            name: String,
            path: String = name,
            builder: AssignmentBuilder.() -> Unit
        ) {
            val assignmentBuilder = AssignmentBuilder(name, path)
            assignmentBuilder.builder()
            val assignment = assignmentBuilder.build()
            assignment.evaluate()
        }
    }

    class AssignmentBuilder(val name: String, path: String) {

        val path: Path = Path.of(path)

        val questions: MutableList<QuestionBuilder> = mutableListOf()
        val users: MutableList<UsersBuilder> = mutableListOf()

        fun addQuestion(name: String, builder: QuestionBuilder.() -> Unit) {
            val question = QuestionBuilder(name)
            question.builder()
            questions.add(question)
        }

        fun build(): Assignment {
            return Assignment(name, path).also { assignment ->
                assignment.questions = questions.map { it.build(assignment) }
                assignment.users = users.map { it.build() }.flatten()
            }
        }

        fun addUsers(builder: UsersBuilder.() -> Unit) {
            val usersBuilder = UsersBuilder(path)
            usersBuilder.builder()
            users.add(usersBuilder)
        }

        fun setMistakes(setter: () -> Unit) {
            setter()
        }

    }

}
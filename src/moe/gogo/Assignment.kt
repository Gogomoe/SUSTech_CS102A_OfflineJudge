package moe.gogo

import java.io.File

class Assignment(root: String, private val questionList: List<String>) {

    val path = File(root).toPath()

    val questions: List<Question> = questionList.map { Question(this, it) }

    val users: List<User> = path.toFile()
        .listFiles { it -> it.isDirectory && it.name !in questionList }
        .map { User(this, it.name) }

    fun evaluate() {
        for (user in users) {
            Console.println(user)
            user.evaluate()
        }
    }

    private fun User.evaluate() {
        for (question in questions) {
            val process = this.evaluators[question]
            val result = process!!.evaluate()
            result.show()
        }
    }


}
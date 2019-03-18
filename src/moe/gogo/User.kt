package moe.gogo

class User(val assignment: Assignment, val name: String) {

    val path = assignment.path.resolve(name).resolve("Submission attachment(s)")

    val loader = Loader(path)

    val processes: MutableMap<Question, Process> = mutableMapOf()

    init {
        assignment.questions.forEach {
            val file = path.resolve("${it.name}.java").toFile()
            val process = Process(this, it, file)
            processes[it] = process
        }
    }

    override fun toString(): String = name


}
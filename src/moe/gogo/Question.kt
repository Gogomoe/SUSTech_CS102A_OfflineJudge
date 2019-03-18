package moe.gogo

import java.nio.file.Files

class Question(val assignment: Assignment, val name: String) {

    val path = assignment.path.resolve(name)

    val cases = mutableListOf<TestCase>()

    init {
        for (i in 1..10) {
            if (!Files.exists(path.resolve("case$i.input"))) {
                break
            }
            cases.add(TestCase(this, path, i))
        }
    }

    override fun toString(): String = name

}
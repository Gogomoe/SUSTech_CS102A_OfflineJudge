package moe.gogo

import moe.gogo.check.Checker
import moe.gogo.check.CompleteChecker
import java.nio.file.Files

class Question(val assignment: Assignment, val name: String) {

    val path = assignment.path.resolve(name)

    val cases = mutableListOf<Case>()

    val checker: Checker = CompleteChecker()

    init {
        for (i in 1..10) {
            if (!Files.exists(path.resolve("case$i.input"))) {
                break
            }
            cases.add(Case(this, path, i))
        }
    }

    override fun toString(): String = name

}
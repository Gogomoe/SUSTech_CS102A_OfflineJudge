package moe.gogo

import java.io.File
import java.nio.file.Path

class TestCase(val question: Question, val path: Path, val No: Int) {

    val fullName = "${question.name}C$No"

    private val filename = "case$No"

    val input: File = path.resolve("$filename.input").toFile()

    var argsFile: File = path.resolve("$filename.args").toFile()
    val args: Array<String> = argsFile.readText().split(" ").toTypedArray()

    val answer: String = path.resolve("$filename.answer").toFile().readText()

}
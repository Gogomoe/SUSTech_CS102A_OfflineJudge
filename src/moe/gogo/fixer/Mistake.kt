package moe.gogo.fixer

enum class Mistake {
    InputWrong, MultiScanner, EncodeWithBOM, SystemExit, FormatError;

    var score: Int = 0
    var maxScore: Int = 0
}

fun List<Mistake>.show() = if (this.isEmpty()) "" else " $this"
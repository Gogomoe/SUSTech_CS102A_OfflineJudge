package moe.gogo.fixer

enum class Mistake {
    INPUT_WRONG, MULTI_SCANNER
}

fun List<Mistake>.show() = if (this.isEmpty()) "" else " $this"
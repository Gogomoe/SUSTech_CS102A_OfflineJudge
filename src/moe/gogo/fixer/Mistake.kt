package moe.gogo.fixer

enum class Mistake {
    INPUT_WRONG
}

fun List<Mistake>.show() = if (this.isEmpty()) "" else " $this"
package moe.gogo.fixer

enum class Mistake {
    INPUT_WRONG, MULTI_SCANNER, ENCODE_WITH_BOM, SYSTEM_EXIT
}

fun List<Mistake>.show() = if (this.isEmpty()) "" else " $this"
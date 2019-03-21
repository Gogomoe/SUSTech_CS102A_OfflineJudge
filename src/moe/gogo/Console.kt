package moe.gogo

import java.util.*

object Console {

    val stdout = System.out!!

    fun print(obj: Any?) = stdout.print(obj)

    fun println(obj: Any?) = stdout.println(obj)

    fun newline() = stdout.println()

    fun error(obj: Any?) = printColorful(obj, "\u001B[31m")

    fun accept(obj: Any?) = printColorful(obj, "\u001B[32m")

    fun warning(obj: Any?) = printColorful(obj, "\u001B[33m")

    private fun printColorful(obj: Any?, color: String) {
        val str = Objects.toString(obj)
        if (str.isNotEmpty()) {
            print("$color$str\u001B[0m")
        }
    }

}
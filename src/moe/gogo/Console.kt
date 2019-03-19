package moe.gogo

object Console {

    val stdout = System.out!!

    fun println(obj: Any?) = stdout.println(obj)

    fun error(obj: Any?) = println("\u001B[31m$obj\u001B[0m")

    fun accept(obj: Any?) = println("\u001B[32m$obj\u001B[0m")

    fun warning(obj: Any?) = println("\u001B[33m$obj\u001B[0m")

}
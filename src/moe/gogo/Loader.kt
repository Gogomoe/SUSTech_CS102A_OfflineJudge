package moe.gogo

import java.nio.file.Files
import java.nio.file.Path

class Loader(val path: Path) : ClassLoader() {

    override fun findClass(name: String?): Class<*> {
        val array: ByteArray = Files.readAllBytes(path.resolve(name!!.className()))
        return defineClass(name, array, 0, array.size)
    }

    companion object {
        private fun String.className(): String = "$this.class"
    }

}


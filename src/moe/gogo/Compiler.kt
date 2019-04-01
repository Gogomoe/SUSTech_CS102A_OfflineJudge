package moe.gogo

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import javax.tools.ToolProvider

class Compiler {

    companion object {
        private val compiler = ToolProvider.getSystemJavaCompiler()

        fun compile(vararg files: File) {
            val fileManager = compiler.getStandardFileManager(null, null, UTF_8)
            val compilationUnits1 = fileManager.getJavaFileObjectsFromFiles(files.toList())
            compiler.getTask(null, fileManager, null, null, null, compilationUnits1).call()
            fileManager.close()
        }

    }

}
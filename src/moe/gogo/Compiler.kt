package moe.gogo

import java.io.File
import javax.tools.ToolProvider

class Compiler {

    companion object {
        private val compiler = ToolProvider.getSystemJavaCompiler()

        fun compile(vararg files: File) {
            val fileManager = compiler.getStandardFileManager(null, null, null)
            val compilationUnits1 = fileManager.getJavaFileObjectsFromFiles(files.toList())
            compiler.getTask(null, fileManager, null, null, null, compilationUnits1).call()
            fileManager.close()
        }

    }

}
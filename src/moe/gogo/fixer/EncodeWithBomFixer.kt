package moe.gogo.fixer

import moe.gogo.Loader

class EncodeWithBomFixer : PreMistakeFixer() {

    private fun Byte.hex(): String = String.format("%x", this)

    override fun haveMistake(state: EvaluatorState): Boolean {
        if (!state.source.exists()) {
            return false
        }
        val bytes = state.source.readBytes()

        if (bytes[0].hex() == "fe" && bytes[1].hex() == "ff") {
            return true
        } else if (bytes[0].hex() == "ff" && bytes[1].hex() == "fe") {
            return true
        } else if (bytes[0].hex() == "ef" && bytes[1].hex() == "bb" && bytes[2].hex() == "bf") {
            return true
        }

        return false
    }

    override fun fix(state: EvaluatorState): EvaluatorState {
        val bytes = state.source.readBytes()

        var fixed: ByteArray? = null
        if (bytes[0].hex() == "fe" && bytes[1].hex() == "ff") {
            fixed = bytes.slice(2 until bytes.size).toByteArray()
        } else if (bytes[0].hex() == "ff" && bytes[1].hex() == "fe") {
            fixed = bytes.slice(2 until bytes.size).toByteArray()
        } else if (bytes[0].hex() == "ef" && bytes[1].hex() == "bb" && bytes[2].hex() == "bf") {
            fixed = bytes.slice(3 until bytes.size).toByteArray()
        }

        val root = state.userpath.resolve("EncodeWithBom")
        val newSource = root.resolve(state.source.name).toFile()
        newSource.parentFile.mkdirs()
        newSource.createNewFile()
        newSource.writeBytes(fixed!!)
        val newLoader = Loader(root)

        state.source = newSource
        state.loader = newLoader
        state.toClear.add { root.toFile() }
        state.requireCompile()
        return state
    }

    override fun addMistake(state: EvaluatorState) {
        state.addMistake(Mistake.ENCODE_WITH_BOM)
    }

}
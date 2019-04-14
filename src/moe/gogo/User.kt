package moe.gogo

import java.nio.file.Path

class User(val name: String, val sourcePath: Path) {

    val path = sourcePath

    val loader = Loader(path)

    override fun toString(): String = name

}

class UsersBuilder(val root: Path) {

    fun build(): List<User> {
        val searchPath = root.resolve(searchPath)
        val users = searchPath.toFile().list { _, name -> name.matches(userMatcher) }
        return users.toList().map {
            User(
                it,
                root.resolve(sourceRoot.replace("%name%", it))
            )
        }
    }

    var searchPath: String = "."
    var userMatcher: Regex = """.*""".toRegex()
    var sourceRoot: String = "%name%"
}
package moe.gogo

fun main() {
    Assignment("Assignment1") {
        addQuestion("A1Q1") {
            score = 20

            addCase {
                argsString = """3"""
                answer = """
                    18.84
                    28.26
                """.trimIndent()
            }
        }

        addUsers {
            searchPath = "."
            userMatcher = """.*\(\d+\)""".toRegex()

            sourceRoot = """%name%/Submission attachment(s)"""
        }
    }
}
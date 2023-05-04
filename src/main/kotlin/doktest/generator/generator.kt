package doktest.generator

import doktest.extractor.Option
import doktest.extractor.RawDocTest

data class DocTest(val content: String, val lineNumbers: IntRange, val pkg: String)

fun generateDocTest(doc: RawDocTest, pkg: String, generatedName: String): DocTest {
    val imports = listOf("import $pkg.*", "import kotlin.test.*") + doc.content.filter { it.startsWith("import ") }
    val rawContent = doc.content.filter { it !in imports }
    val content = when (doc.option) {
        Option.RUN ->
            """
            |${imports.joinToString("\n")}
            |
            |class $generatedName {
            |    @Test
            |    fun main() {
            |${rawContent.joinToString("\n") { it.prependIndent(" ".repeat(8)) }}
            |    }
            |}
            """.trimMargin()

        Option.NORUN ->
            """
            |${imports.joinToString("\n")}
            |
            |fun main() {
            |${rawContent.joinToString("\n") { it.prependIndent(" ".repeat(4)) }}
            |}
            """.trimMargin()

        Option.NOMAIN ->
            """
            |package $generatedName
            |
            |${imports.joinToString("\n")}
            |
            |${rawContent.joinToString("\n")}
            """.trimMargin()
    }
    return DocTest(content, doc.lineNumbers, pkg)
}
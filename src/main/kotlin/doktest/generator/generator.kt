package doktest.generator

import doktest.extractor.Option
import doktest.extractor.RawDocTest

data class DocTest(val content: String, val lineNumbers: IntRange, val pkg: String)

fun generateDocTest(doc: RawDocTest, pkg: String): DocTest {
    val imports = listOf("import $pkg.*", "import kotlin.test.*") + doc.content.filter { it.startsWith("import ") }
    val rawContent = doc.content.filter { it !in imports }
    val content = when (doc.option) {
        Option.RUN -> TODO()

        Option.NORUN ->
            """
            |${imports.joinToString("\n")}
            |
            |fun main() {
            |${rawContent.joinToString("\n")}
            |}
            """.trimMargin()

        Option.NOMAIN ->
            """
            |${imports.joinToString("\n")}
            |
            |${rawContent.joinToString("\n")}
            """.trimMargin()
    }
    return DocTest(content, doc.lineNumbers, pkg)
}
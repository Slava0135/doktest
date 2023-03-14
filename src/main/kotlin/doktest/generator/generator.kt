package doktest.generator

import doktest.extractor.RawDocTest

data class DocTest(val content: String, val lineNumbers: IntRange, val pkg: String)

fun generateDocTest(doc: RawDocTest, pkg: String): DocTest {
    val imports = listOf("import $pkg.*", "import kotlin.test.*") + doc.content.filter { it.startsWith("import ") }
    val main = doc.content.filter { it !in imports }
    val content = """
        |${imports.joinToString("\n")}
        |
        |fun main() {
        |${main.joinToString("\n")}
        |}
    """.trimMargin()
    return DocTest(content, doc.lineNumbers, pkg)
}
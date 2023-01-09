package doktest.generator

import doktest.extractor.RawDocTest

data class DocTest(val content: String, val lineNumbers: IntRange)

fun generateDocTest(doc: RawDocTest, pkg: String): DocTest {
    val content = """
        |import $pkg.*
        |import kotlin.test.*
        |
        |fun main() {
        |${doc.content.joinToString("\n")}
        |}
    """.trimMargin()
    return DocTest(content, doc.lineNumbers)
}
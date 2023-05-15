package io.github.slava0135.doktest.generator

import io.github.slava0135.doktest.extractor.Option
import io.github.slava0135.doktest.extractor.RawDocTest

data class DocTest(val content: String, val lineNumbers: IntRange, val pkg: String)

fun generateDocTest(doc: RawDocTest, pkg: String, generatedName: String): DocTest {
    val defaultImports = listOf("import $pkg.*", "import kotlin.test.*")
    val docImports = doc.content.filter { it.startsWith("import ") }
    val allImports = defaultImports + docImports
    val rawContent = doc.content.filter { it !in allImports }
    val content = when (doc.option) {
        Option.RUN ->
            """
            |${allImports.joinToString("\n")}
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
            |${allImports.joinToString("\n")}
            |
            |fun main() {
            |${rawContent.joinToString("\n") { it.prependIndent(" ".repeat(4)) }}
            |}
            """.trimMargin()

        Option.NOMAIN ->
            """
            |package $generatedName
            |
            |${allImports.joinToString("\n")}
            |
            |${rawContent.joinToString("\n")}
            """.trimMargin()
    }
    return DocTest(content, doc.lineNumbers, pkg)
}
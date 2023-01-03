package doktest.extractor

import kotlin.test.*

class ExtractorKtTest {

    @Test
    fun `test extract package with success`() {
        val packageName = "foo.bar"
        val result = extractPackage("""
            |package $packageName
        """.trimMargin())
        assertEquals(packageName, result)
    }

    @Test
    fun `test extract package with failure`() {
        assertEquals(null, extractPackage(""))
    }

    @Test
    fun `test extract docs with 1 comment`() {
        val comment = """
            |/**
            | * boo far
            | */
        """.trimMargin()
        val input = comment + """
            |
            |fun foobar() {
            |   println("foobar")
            |}
        """.trimMargin()
        val expect = listOf(Doc(comment.trimMargin(), 1..3))
        assertEquals(expect, extractDocs(input))
    }
}
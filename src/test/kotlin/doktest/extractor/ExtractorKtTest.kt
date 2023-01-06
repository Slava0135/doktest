package doktest.extractor

import doktest.extractor.helpers.*
import kotlin.test.*

class ExtractorKtTest {

    @Test
    fun `test extract package with success`() {
        val packageName = "foo.bar"
        val result = extractPackage(
            """
            |package $packageName
        """.trimMargin()
        )
        assertEquals(packageName, result)
    }

    @Test
    fun `test extract package with failure`() {
        assertEquals(null, extractPackage(""))
    }

    @Test
    fun `test extract all raw doc tests`() {
        val input = """
            |/** 0
            | * ```kotlin doctest
            | *     foobar()
            | * ```
            | */ 4
            |fun foobar() {
            |    println("foobar")
            |}
            |/** 8
            | * ```kotlin
            | *     println("h")
            | * ```
            | */ 12
            |class boofar() {
            |    /** 14
            |     *  15
            |     * ```kotlin doctest
            |     * foobaz()
            |     * foobaz()
            |     * ```
            |     *  20
            |     *  21
            |     *  22
            |     * ```kotlin doctest
            |     * foobaz()
            |     * foobar()
            |     * println("foobaz")
            |     * ```
            |     */ 28
            |    fun foobaz() {
            |       println("foobaz")
            |    }
            |}
        """.trimMargin()
        val expect = listOf(
            RawDocTest(listOf("    foobar()"), 1..3),
            RawDocTest(listOf("foobaz()", "foobaz()"), 16..19),
            RawDocTest(listOf("foobaz()", "foobar()", "println(\"foobaz\")"), 23..27),
        )
        assertEquals(expect, extractAllRawDocTests(input))
    }

}
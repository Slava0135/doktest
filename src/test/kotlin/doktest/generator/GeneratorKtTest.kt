package doktest.generator

import doktest.extractor.Option
import doktest.extractor.RawDocTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GeneratorKtTest {

    @Test
    fun `test generate doc test no imports`() {
        val expect = """
            |import foo.bar.*
            |import kotlin.test.*
            |
            |fun main() {
            |    foobaz()
            |    bazfoo()
            |}
        """.trimMargin()
        assertEquals(
            expect,
            generateDocTest(
                RawDocTest(listOf("    foobaz()", "    bazfoo()"), -1..-1, Option.NORUN),
                "foo.bar"
            ).content
        )
    }

    @Test
    fun `test generate doc test with imports`() {
        val expect = """
            |import foo.bar.*
            |import kotlin.test.*
            |import bar.foo
            |import foo.foo
            |
            |fun main() {
            |    foobaz()
            |    bazfoo()
            |}
        """.trimMargin()
        assertEquals(
            expect,
            generateDocTest(
                RawDocTest(
                    listOf("import bar.foo", "import foo.foo", "    foobaz()", "    bazfoo()"),
                    -1..-1,
                    Option.NORUN
                ),
                "foo.bar"
            ).content
        )
    }

    @Test
    fun `test generate doc test with nomain option`() {
        val expect = """
            |import foo.bar.*
            |import kotlin.test.*
            |
            |class Foo {
            |    val bar = 0
            |}
        """.trimMargin()
        assertEquals(
            expect,
            generateDocTest(
                RawDocTest(listOf("class Foo {", "    val bar = 0", "}"), -1..-1, Option.NOMAIN),
                "foo.bar"
            ).content
        )
    }
}

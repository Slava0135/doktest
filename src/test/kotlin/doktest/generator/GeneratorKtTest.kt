package doktest.generator

import doktest.extractor.helpers.RawDocTest
import kotlin.test.*

class GeneratorKtTest {

    @Test
    fun `test generate doc test`() {
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
                RawDocTest(listOf("    foobaz()", "    bazfoo()"), -1..-1),
                "foo.bar"
            ).content
        )
    }

}
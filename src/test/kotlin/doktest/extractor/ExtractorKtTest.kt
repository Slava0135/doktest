package doktest.extractor

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
    fun `test extract doc indices with 1 comment`() {
        val input = """
            |/**
            | * boo far
            | */
            |fun foobar() {
            |   println("foobar")
            |}
        """.trimMargin()
        val expect = listOf(0..2)
        assertEquals(expect, extractDocIndices(input))
    }

    @Test
    fun `test extract doc indices with 2 comments and blank line`() {
        val input = """
            |/**
            | * boo far
            | */
            |fun foobar() {
            |   println("foobar")
            |}
            |class Zoo() {
            |   /**
            |    * zoofaz
            |   
            |    */
            |   fun zoofaz() {
            |       println("zoofaz)
            |   }
            |}
        """.trimMargin()
        val expect = listOf(0..2, 7..10)
        assertEquals(expect, extractDocIndices(input))
    }

    @Test
    fun `test extract doc indices unaligned`() {
        val input = """
            |/**
            |*  test1
            | */
            |class test1() {
            |   /**
            |    * test2
            |   */
            |   class test2() {
            |       /**
            |       *
            |       */
            |       class test3() {
            |           /** test4
            |           */
            |           class test4() {
            |           }
            |       }
            |   }
            |}
        """.trimMargin()
        assertEquals(emptyList(), extractDocIndices(input))
    }

    @Test
    fun `test extract doc content`() {
        val input = """
            |   /**
            |    * foobar
            |    *     indent
            |
            |    *bla bla bla
            |    */
        """.trimMargin()
        val expect = """
            |foobar
            |    indent
            |
            |bla bla bla
        """.trimMargin()
        assertEquals(expect.lines(), extractDocContent(input.lines()))
    }

    @Test
    fun `test extract docs`() {
        val input = """
            |/**
            | * boo far
            | */
            |fun foobar() {
            |   println("foobar")
            |}
            |class Zoo() {
            |   /**
            |    * zoofaz
            |   
            |    */
            |   fun zoofaz() {
            |       println("zoofaz)
            |   }
            |}
        """.trimMargin()
        val expect = listOf(
            Doc(listOf("boo far"), 1..1),
            Doc(listOf("zoofaz", ""), 8..9)
        )
        assertEquals(expect, extractDocs(input))
    }
}
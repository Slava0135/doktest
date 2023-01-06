package doktest.extractor.helpers

import kotlin.test.*

class HelpersKtTest {
    @Test
    fun `test extract docs`() {
        val input = """
            |/** fa
            | * boo far
            | */    la
            |fun foobar() {
            |   println("foobar")
            |}
            |class Zoo() {
            |   /**
            |    * zoofaz
            |   
            |    */ ooo
            |   fun zoofaz() {
            |       println("zoofaz)
            |   }
            |}
        """.trimMargin()
        val expect = listOf(
            Doc(listOf(" fa", "boo far", "    la"), 0..2),
            Doc(listOf("", "zoofaz", "", " ooo"), 7..10)
        )
        assertEquals(expect, extractDocs(input))
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
            |   /** first
            |    * foobar
            |    *     indent
            |
            |    *bla bla bla
            |    */  la last
        """.trimMargin()
        val expect = """
            | first
            |foobar
            |    indent
            |
            |bla bla bla
            |  la last
        """.trimMargin()
        assertEquals(expect.lines(), extractDocContent(input.lines()))
    }

    @Test
    fun `test extract raw doc tests`() {
        val content = """
            |0
            |1
            |```kotlin doctest 
            |    println("hello")
            |    println("world")
            |```
            |6
            |7
            |8
            |```kotlin
            |    println("not doctest")
            |```
            |12
            |13
            |```kotlin doctest
            |    println("foobar")
            |```
            |17
            |18
        """.trimMargin()
        val doc = Doc(content.lines(), 10..27)
        val expect = listOf(
            RawDocTest(listOf("    println(\"hello\")", "    println(\"world\")"), 12..15),
            RawDocTest(listOf("    println(\"foobar\")"), 24..26)
        )
        assertEquals(expect, extractRawDocTests(doc))
    }
}
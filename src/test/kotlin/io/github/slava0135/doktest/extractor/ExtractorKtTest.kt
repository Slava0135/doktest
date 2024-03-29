package io.github.slava0135.doktest.extractor

import kotlin.test.Test
import kotlin.test.assertEquals

class ExtractorKtTest {

    @Test
    fun `test extract package with success`() {
        val packageName = "foo.bar"
        val result = extractPackage(
            """
            |package $packageName
            |
            |import bla.bla
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
            | */
            |fun foobar() {
            |    println("foobar")
            |}
            |/** 8
            | * ```kotlin
            | *     println("h")
            | * ```
            | */
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
            |     * foobar()
            |     * foobaz()
            |
            |     * ```
            |
            |     * 28
            |     */
            |    fun foobaz() {
            |       println("foobaz")
            |    }
            |}
        """.trimMargin()
        val expect = listOf(
            RawDocTest(listOf("    foobar()"), 1..3, Option.RUN),
            RawDocTest(listOf("foobaz()", "foobaz()"), 16..19, Option.RUN),
            RawDocTest(listOf("foobar()", "foobaz()", ""), 23..27, Option.RUN),
        )
        assertEquals(expect, extractAllRawDocTests(input))
    }

    @Test
    fun `test extract all raw doc tests invalid inputs`() {
        val input = """
            | /**
            | * ```kotlin doctest
            | *
            | * ```
            | */
            | 
            |/**
            | * ```kotlin doctest
            |  *
            | * ```
            | */
            | 
            |/**
            | * ```kotlin doctest
            | *
            | * ```
            |  */
            |
            |/**
            | * ```kotlin doctest
            | *
            | */```
            | 
            |/**
            | *  ```kotlin doctest
            | *
            | * ```
            | */
        """.trimMargin()
        assertEquals(emptyList(), extractAllRawDocTests(input))
    }

    @Test
    fun `test extract all raw doc tests options`() {
        val input = """
            |/**
            | * ```kotlin doctest
            | *     foobar()
            | * ```
            | */
            |
            |/**
            | * ```kotlin doctest:run
            | *     foobar()
            | * ```
            | */
            | 
            |/**
            | * ```kotlin doctest:norun
            | *     foobar()
            | * ```
            | */
            | 
            |/**
            | * ```kotlin doctest:nomain
            | *     foobar()
            | * ```
            | */
            | 
            |/**
            | * ```kotlin doctest:nonsense
            | *     foobar()
            | * ```
            | */
        """.trimMargin()
        val expect = listOf(
            Option.RUN,
            Option.RUN,
            Option.NORUN,
            Option.NOMAIN,
            Option.RUN
        )
        assertEquals(expect, extractAllRawDocTests(input).map { it.option })
    }
}
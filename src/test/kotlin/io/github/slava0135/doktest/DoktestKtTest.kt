package io.github.slava0135.doktest

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DoktestKtTest {
    @TempDir
    lateinit var testProjectDir: File
    private lateinit var settingsFile: File
    private lateinit var buildFile: File

    private val mainSrc = "/src/main/kotlin"

    private fun readFileFromResource(from: String, to: String): File {
        return File(testProjectDir, to).apply {
            writeText(javaClass.getResource(from)!!.readText())
        }
    }

    @BeforeEach
    fun setup() {
        settingsFile = readFileFromResource("/build/settings.gradle.kts", "settings.gradle.kts")
        buildFile = readFileFromResource("/build/build.gradle.kts", "build.gradle.kts")
        Files.createDirectories(Paths.get(testProjectDir.path + mainSrc))
    }

    @Test
    fun `test empty`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME)
            .withPluginClasspath()
            .build()
        assertEquals(TaskOutcome.SUCCESS, result.task(":$DOKTEST_TASK_NAME")!!.outcome)
        assertTrue(result.tasks(TaskOutcome.FAILED).isEmpty())
    }

    @Test
    fun `test simple`() {
        readFileFromResource("/cases/simple.kt", "$mainSrc/simple.kt")
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME)
            .withPluginClasspath()
            .build()
        assertEquals(TaskOutcome.SUCCESS, result.task(":$DOKTEST_TASK_NAME")!!.outcome)
    }

    @Test
    fun `test simple fail`() {
        readFileFromResource("/cases/simple_fail.kt", "$mainSrc/simple_fail.kt")
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME)
            .withPluginClasspath()
            .buildAndFail()
        assertEquals(TaskOutcome.SUCCESS, result.task(":$DOKTEST_TASK_NAME")!!.outcome) // should this fail?
    }

    @Test
    fun `test all options`() {
        readFileFromResource("/cases/all_options.kt", "$mainSrc/all_options.kt")
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME)
            .withPluginClasspath()
            .build()
        assertEquals(TaskOutcome.SUCCESS, result.task(":$DOKTEST_TASK_NAME")!!.outcome)
    }

    @Test
    fun `test norun option fail`() {
        readFileFromResource("/cases/norun_fail.kt", "$mainSrc/norun_fail.kt")
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME)
            .withPluginClasspath()
            .buildAndFail()
        assertEquals(TaskOutcome.SUCCESS, result.task(":$DOKTEST_TASK_NAME")!!.outcome)
    }

    @Test
    fun `test nomain option fail`() {
        readFileFromResource("/cases/nomain_fail.kt", "$mainSrc/nomain_fail.kt")
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME)
            .withPluginClasspath()
            .buildAndFail()
        assertEquals(TaskOutcome.SUCCESS, result.task(":$DOKTEST_TASK_NAME")!!.outcome)
    }

    @Test
    fun `test file option`() {
        readFileFromResource("/cases/two_files/good.kt", "$mainSrc/good.kt")
        readFileFromResource("/cases/two_files/bad.kt", "$mainSrc/bad.kt")
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME)
            .withPluginClasspath()
            .buildAndFail()
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME, "--file", "good")
            .withPluginClasspath()
            .build()
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME, "--file", "bad")
            .withPluginClasspath()
            .buildAndFail()
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME, "--file", "unknown")
            .withPluginClasspath()
            .buildAndFail()
    }

    @Test
    fun `test import`() {
        File(testProjectDir, "$mainSrc/test_import").mkdirs()
        readFileFromResource("/cases/test_import/good.kt", "$mainSrc/test_import/good.kt")
        readFileFromResource("/cases/simple.kt", "$mainSrc/simple.kt")
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME)
            .withPluginClasspath()
            .build()
        readFileFromResource("/cases/test_import/bad.kt", "$mainSrc/test_import/bad.kt")
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME)
            .withPluginClasspath()
            .buildAndFail()
    }

    @Test
    fun `test line option`() {
        readFileFromResource("/cases/line_option.kt", "$mainSrc/line_option.kt")
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME)
            .withPluginClasspath()
            .buildAndFail()
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME, "--file", ".kt", "--line", "4")
            .withPluginClasspath()
            .buildAndFail()
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME, "--file", ".kt", "--line", "10")
            .withPluginClasspath()
            .buildAndFail()
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME, "--file", ".kt", "--line", "14")
            .withPluginClasspath()
            .build()
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME, "--file", ".kt", "--line", "666")
            .withPluginClasspath()
            .buildAndFail()
    }

    @Test
    fun `test no redeclaration`() {
        readFileFromResource("/cases/no_redeclaration.kt", "$mainSrc/no_redeclaration.kt")
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME)
            .withPluginClasspath()
            .build()
    }

    @Test
    fun `test no doctests in file`() {
        readFileFromResource("/cases/no_doctests.kt", "$mainSrc/no_doctests.kt")
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME)
            .withPluginClasspath()
            .build()
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME, "--file", ".kt")
            .withPluginClasspath()
            .buildAndFail()
    }

    @Test
    fun `test line option no file provided`() {
        readFileFromResource("/cases/simple.kt", "$mainSrc/simple.kt")
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME, "--line", "42")
            .withPluginClasspath()
            .buildAndFail()
    }

    @Test
    fun `test version compatibility`() {
        readFileFromResource("/cases/simple.kt", "$mainSrc/simple.kt")
        readFileFromResource("/cases/simple_fail.kt", "$mainSrc/simple_fail.kt")
        val versions = listOf("8.1.1", "7.6.1", "6.9.4")
        for (ver in versions) {
            GradleRunner.create()
                .withGradleVersion(ver)
                .withProjectDir(testProjectDir)
                .withArguments(DOKTEST_TASK_NAME, "--file", "simple", "--line", "4")
                .withPluginClasspath()
                .build()
            GradleRunner.create()
                .withGradleVersion(ver)
                .withProjectDir(testProjectDir)
                .withArguments(DOKTEST_TASK_NAME, "--file", "simple_fail", "--line", "4")
                .withPluginClasspath()
                .buildAndFail()
        }
    }

    @Test
    fun `run performance test`() {
        val prelude = """
            |package main
            |
            |fun plus(a: Int, b: Int) = a + b
        """.trimMargin()
        val file = File(testProjectDir, "$mainSrc/main.kt")
        file.writeText("$prelude\n")
        repeat(1000) {
            val doc = """
                |/**
                | * ```kotlin doctest
                | * val a = $it
                | * val b = ${it + 1}
                | * assertEquals(${it + it + 1}, plus(a, b))
                | * ```
                | */
            """.trimMargin()
            file.appendText("$doc\n")
        }
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME)
            .withPluginClasspath()
            .build()
    }
}
package doktest

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
    }
}
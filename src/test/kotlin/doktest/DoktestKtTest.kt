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

    @BeforeEach
    fun setup() {
        settingsFile = File(testProjectDir, "settings.gradle.kts").apply {
            writeText(javaClass.getResource("/build/settings.gradle.kts")!!.readText())
        }
        buildFile = File(testProjectDir, "build.gradle.kts").apply {
            writeText(javaClass.getResource("/build/build.gradle.kts")!!.readText())
        }
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
        File(testProjectDir, "$mainSrc/simple.kt").apply {
            writeText(javaClass.getResource("/simple/simple.kt")!!.readText())
        }
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(DOKTEST_TASK_NAME)
            .withPluginClasspath()
            .build()
        assertEquals(TaskOutcome.SUCCESS, result.task(":$DOKTEST_TASK_NAME")!!.outcome)
        assertTrue(result.tasks(TaskOutcome.FAILED).isEmpty())
    }
}
package doktest

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DoktestKtTest {
    @TempDir
    lateinit var testProjectDir: File
    private lateinit var settingsFile: File
    private lateinit var buildFile: File

    @BeforeEach
    fun setup() {
        settingsFile = File(testProjectDir, "settings.gradle.kts").apply {
            writeText(javaClass.getResource("/build/settings.gradle.kts")!!.readText())
        }
        buildFile = File(testProjectDir, "build.gradle.kts").apply {
            writeText(javaClass.getResource("/build/build.gradle.kts")!!.readText())
        }
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
}
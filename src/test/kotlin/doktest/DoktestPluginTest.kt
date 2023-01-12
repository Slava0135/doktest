package doktest

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull

const val PLUGIN_ID = "com.github.slava0135.doktest"

class DoktestPluginTest {
    @Test
    fun `test that plugin is applied`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(PLUGIN_ID)
        assertNotNull(project.plugins.getPlugin(PLUGIN_ID))
    }

    @Test
    fun `test that task is registered`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(PLUGIN_ID)
        assertNotNull(project.tasks.getByName("doktest"))
    }
}

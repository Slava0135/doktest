package doktest

import org.gradle.api.Plugin
import org.gradle.api.Project

class DoktestPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.create("doktest", Doktest::class.java)
    }
}

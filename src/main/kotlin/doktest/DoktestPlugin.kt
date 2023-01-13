package doktest

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

const val SOURCE_SET_NAME = "doktest"

class DoktestPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val sourceSets = target.extensions.getByType(SourceSetContainer::class.java)
        val test = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)
        val set = sourceSets.create("doktest")
        set.apply {
            set.compileClasspath += test.compileClasspath
            set.runtimeClasspath += test.runtimeClasspath
        }
        val compileKotlin = set.getCompileTaskName("kotlin")
        val compileKotlinTask = target.tasks.getByName(compileKotlin)
        val task = target.tasks.create("doktest", Doktest::class.java)
        task.finalizedBy(compileKotlinTask)
    }
}

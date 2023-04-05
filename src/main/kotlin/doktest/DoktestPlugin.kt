package doktest

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test

const val SOURCE_SET_NAME = "doktest"

class DoktestPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val sourceSets = target.extensions.getByType(SourceSetContainer::class.java)
        val testSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)
        val doktestSet = sourceSets.create("doktest")
        doktestSet.apply {
            doktestSet.compileClasspath += testSet.compileClasspath
            doktestSet.runtimeClasspath += testSet.runtimeClasspath
        }
        val doktestTask = target.tasks.create("doktest", Doktest::class.java)
        val compileKotlin = doktestSet.getCompileTaskName("kotlin")
        val compileKotlinTask = target.tasks.getByName(compileKotlin)
        val doktestTestTask = target.tasks.create("doktestTest", Test::class.java).apply {
            testClassesDirs = doktestSet.output.classesDirs
            classpath = doktestSet.runtimeClasspath
            useJUnitPlatform()
        }
        doktestTask.finalizedBy(compileKotlinTask, doktestTestTask)
    }
}

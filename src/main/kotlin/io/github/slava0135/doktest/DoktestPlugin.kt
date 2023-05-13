package io.github.slava0135.doktest

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test

const val SOURCE_SET_NAME = "doktest"
const val DOKTEST_TASK_NAME = "doktest"
const val DOKTEST_TEST_TASK_NAME = "doktestTest"

@Suppress("unused")
class DoktestPlugin : Plugin<Project> {
    override fun apply(target: Project) {

        val sourceSets = target.extensions.getByType(SourceSetContainer::class.java)
        val testSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)
        val doktestSet = sourceSets.create(SOURCE_SET_NAME).apply {
            compileClasspath += testSet.compileClasspath
            runtimeClasspath += testSet.runtimeClasspath
        }

        val doktestTask = target.tasks.create(DOKTEST_TASK_NAME, Doktest::class.java)

        val compileKotlinTaskName = doktestSet.getCompileTaskName("kotlin")
        val compileKotlinTask = target.tasks.getByName(compileKotlinTaskName)

        val doktestTestTask = target.tasks.create(DOKTEST_TEST_TASK_NAME, Test::class.java).apply {
            testClassesDirs = doktestSet.output.classesDirs
            classpath = doktestSet.runtimeClasspath
            useJUnitPlatform()
        }

        doktestTask.finalizedBy(compileKotlinTask, doktestTestTask)
    }
}

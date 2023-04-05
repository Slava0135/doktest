package doktest

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

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
//        val testTask = target.tasks.named("test") as TaskProvider<Test>
        target.tasks.create("doktest", Doktest::class.java).apply {
//            testFrameworkProperty.set(testTask.get().testFramework)
            useJUnitPlatform()
            testClassesDirs = doktestSet.output.classesDirs
            classpath = doktestSet.runtimeClasspath
        }
    }
}

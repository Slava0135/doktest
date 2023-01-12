package doktest

import doktest.extractor.extractAllRawDocTests
import doktest.extractor.extractPackage
import doktest.generator.generateDocTest
import org.gradle.api.DefaultTask
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction


abstract class Doktest : DefaultTask() {
    @TaskAction
    fun test() {
        project.plugins.withType(JavaPlugin::class.java) { javaPlugin: JavaPlugin ->
            val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
            val main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
            val test = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)
            main.allSource.files.forEach { file ->
                if (file.extension == "kt") {
                    val text = file.readText()
                    val pkg = extractPackage(text)
                    val rawDocTests = extractAllRawDocTests(text)
                    val docTests = rawDocTests.map {
                        generateDocTest(it, pkg!!)
                    }
                    if (pkg != null && docTests.isNotEmpty()) {
                        println(docTests)
//                        val set = sourceSets.create()
//                        try {
//                            set.compileClasspath += test.compileClasspath
//                            set.compileClasspath += main.output
//                        } finally {
//                            sourceSets.remove(set)
//                        }
                    }
                }
            }
        }
    }
}

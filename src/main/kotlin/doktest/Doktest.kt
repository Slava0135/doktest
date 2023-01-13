package doktest

import doktest.extractor.extractAllRawDocTests
import doktest.extractor.extractPackage
import doktest.generator.generateDocTest
import org.gradle.api.DefaultTask
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import java.io.File


abstract class Doktest : DefaultTask() {
    @TaskAction
    fun test() {
        project.plugins.withType(JavaPlugin::class.java) { javaPlugin: JavaPlugin ->
            val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
            val main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
            val set = sourceSets.getByName(SOURCE_SET_NAME)
            main.allSource.files.forEach { file ->
                if (file.extension == "kt") {
                    val text = file.readText()
                    val pkg = extractPackage(text)
                    val rawDocTests = extractAllRawDocTests(text)
                    val docTests = rawDocTests.map {
                        generateDocTest(it, pkg!!)
                    }
                    if (pkg != null && docTests.isNotEmpty()) {
                        docTests.forEach { docTest ->
                            set.resources.setSrcDirs(emptyList<Any>())
                            val dir = temporaryDir
                            dir.listFiles()?.forEach {
                                it.delete()
                            }
                            val testFile = File(dir, "Main.kt")
                            testFile.writeText(docTest.content)
                            set.resources.srcDir(dir.path)
                            println(set.allSource.files)
                        }
                    }
                }
            }
        }
    }
}

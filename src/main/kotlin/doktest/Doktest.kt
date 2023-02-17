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
        project.plugins.withType(JavaPlugin::class.java) {
            val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
            val main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
            val set = sourceSets.getByName(SOURCE_SET_NAME)
            val dir = setupDir(set)
            var fileId = 0
            for (file in main.allSource.files) {
                if (file.extension == "kt") {
                    val text = file.readText()
                    val pkg = extractPackage(text)
                    val rawDocTests = extractAllRawDocTests(text)
                    if (pkg == null) {
                        logger.info("file $file - no package definition detected, skipping")
                        continue
                    }
                    val docTests = rawDocTests.map {
                        generateDocTest(it, pkg)
                    }
                    if (docTests.isEmpty()) {
                        logger.info("file $file - no doctests detected, skipping ")
                        continue
                    }
                    logger.info("file $file detected ${docTests.size} doctests")

                    fileId++
                    docTests.forEach { docTest ->
                        val lineNumbers = (docTest.lineNumbers.first + 1)..(docTest.lineNumbers.last + 1)
                        val testFile = File(dir, "${file.nameWithoutExtension}(${fileId})${lineNumbers}.kt")
                        val testContent = """
                            |// generated from $file - lines ${lineNumbers}
                            |${docTest.content}
                        """.trimMargin()
                        testFile.writeText(testContent)
                        logger.info("  $testFile  ")
                    }
                }
            }
        }
    }

    private fun setupDir(set: SourceSet): File {
        set.java.setSrcDirs(emptyList<Any>())
        val dir = temporaryDir
        dir.listFiles()?.forEach {
            it.deleteRecursively()
        }
        set.java.srcDir(dir.path)
        return dir
    }
}

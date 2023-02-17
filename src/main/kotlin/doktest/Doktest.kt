package doktest

import doktest.extractor.extractAllRawDocTests
import doktest.extractor.extractPackage
import doktest.generator.DocTest
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
            for (file in main.allSource.files) {
                if (file.extension == "kt") {
                    val text = file.readText()
                    val pkg = extractPackage(text)
                    val rawDocTests = extractAllRawDocTests(text)
                    if (pkg == null) {
                        logger.info("File ${file.path} - no package definition detected, skipping")
                        continue
                    }
                    val docTests = rawDocTests.map {
                        generateDocTest(it, pkg)
                    }
                    if (docTests.isEmpty()) {
                        logger.info("File ${file.path} - no doctests detected, skipping ")
                        continue
                    }
                    logger.info("File ${file.path} - detected ${docTests.size} doctests")
                    docTests.forEach { docTest ->
                        val testFile = File(dir, generateTestFileName(file, docTest))
                        logger.info("Generated doctest from line numbers ${docTest.lineNumbers}")
                        testFile.writeText(docTest.content)
                    }
                }
            }
        }
    }

    private fun generateTestFileName(file: File, docTest: DocTest) =
        file.nameWithoutExtension + "_" + docTest.lineNumbers + ".kt"

    private fun setupDir(set: SourceSet): File {
        set.java.srcDir(emptyList<Any>())
        val dir = temporaryDir
        dir.listFiles()?.forEach {
            it.delete()
        }
        set.java.srcDir(dir.path)
        return dir
    }
}

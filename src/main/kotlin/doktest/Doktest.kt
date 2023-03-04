package doktest

import doktest.extractor.extractAllRawDocTests
import doktest.extractor.extractPackage
import doktest.generator.DocTest
import doktest.generator.generateDocTest
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

abstract class Doktest : DefaultTask() {
    private var fileName: String? = null

    @Option(option = "file", description = "Only test single file with specified path suffix")
    fun setFileName(fileName: String) {
        this.fileName = fileName
    }

    @TaskAction
    fun test() {
        project.plugins.withType(JavaPlugin::class.java) {
            val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
            val main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
            val set = sourceSets.getByName(SOURCE_SET_NAME)
            val dir = setupDir(set)
            if (fileName == null) {
                testAllSourceFiles(main.allSource.files, dir)
            } else {
                testSingleSourceFile(main.allSource.files, dir, fileName!!)
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

    private fun testAllSourceFiles(sourceFiles: Set<File>, dir: File) {
        var fileId = 0
        for (file in sourceFiles) {
            if (file.extension == "kt") {
                fileId++
                val docTests = extractAllDoctests(file)
                docTests?.forEach { docTest ->
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

    private fun testSingleSourceFile(sourceFiles: Set<File>, dir: File, fileName: String) {
        val matchingFiles = sourceFiles.filter { it.path.endsWith(fileName) }
        if (matchingFiles.isEmpty()) {
            val msg = "no files ending with '$fileName' found"
            logger.error(msg)
            throw InvalidUserDataException(msg)
        }
        if (matchingFiles.size > 1) {
            val msg = "more than 1 files found ending with '$fileName'"
            logger.error(msg)
            logger.error(matchingFiles.sorted().joinToString("\n"))
            throw InvalidUserDataException(msg)
        }
        val file = matchingFiles.first()
        val docTests = extractAllDoctests(file)
        docTests?.forEach { docTest ->
            val lineNumbers = (docTest.lineNumbers.first + 1)..(docTest.lineNumbers.last + 1)
            val testFile = File(dir, "${file.nameWithoutExtension}${lineNumbers}.kt")
            val testContent = """
                                |// generated from $file - lines ${lineNumbers}
                                |${docTest.content}
                            """.trimMargin()
            testFile.writeText(testContent)
            logger.info("  $testFile  ")
        }
    }

    private fun extractAllDoctests(file: File): List<DocTest>? {
        val text = file.readText()
        val pkg = extractPackage(text)
        val rawDocTests = extractAllRawDocTests(text)
        if (pkg == null) {
            logger.info("file $file - no package definition detected, skipping")
            return null
        }
        val docTests = rawDocTests.map {
            generateDocTest(it, pkg)
        }
        if (docTests.isEmpty()) {
            logger.info("file $file - no doctests detected, skipping ")
            return null
        }
        logger.info("file $file detected ${docTests.size} doctests")
        return docTests
    }
}

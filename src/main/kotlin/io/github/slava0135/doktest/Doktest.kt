package io.github.slava0135.doktest

import io.github.slava0135.doktest.extractor.extractAllRawDocTests
import io.github.slava0135.doktest.extractor.extractPackage
import io.github.slava0135.doktest.generator.DocTest
import io.github.slava0135.doktest.generator.generateDocTest
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
    private var lineNumber: Int? = null

    @Option(option = "file", description = "Only test single file with specified path suffix")
    fun setFileName(fileName: String) {
        this.fileName = fileName
    }

    @Option(option = "line", description = "Only run single test from specified file on the specified line")
    fun setLineNumber(lineNumber: String) {
        val parsed = lineNumber.toIntOrNull()
        if (parsed == null) {
            val msg = "option --line: '$lineNumber' is not Integer"
            logger.error(msg)
            throw InvalidUserDataException(msg)
        }
        this.lineNumber = parsed
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
        for (file in sourceFiles) {
            if (file.extension == "kt") {
                val docTests = extractAllDoctests(file) ?: return
                docTests.forEach { docTest ->
                    val lineNumbers = (docTest.lineNumbers.first + 1)..(docTest.lineNumbers.last + 1)
                    val testFile = File(dir, generateFileName(docTest, file, lineNumbers))
                    val testContent = """
                                |// generated from $file - lines $lineNumbers
                                |${docTest.content}
                            """.trimMargin()
                    testFile.writeText(testContent)
                    logger.info("  $testFile  ")
                }
            }
        }
    }

    private fun testSingleSourceFile(sourceFiles: Set<File>, dir: File, fileName: String) {
        val fileNameWithExt = fileName.removeSuffix(".kt") + ".kt"
        val matchingFiles = sourceFiles.filter { it.path.endsWith(fileNameWithExt) }
        if (matchingFiles.isEmpty()) {
            val msg = "no files ending with '$fileNameWithExt' found"
            logger.error(msg)
            throw InvalidUserDataException(msg)
        }
        if (matchingFiles.size > 1) {
            val msg = "more than 1 files found ending with '$fileNameWithExt'"
            logger.error(msg)
            logger.error(matchingFiles.sorted().joinToString("\n"))
            throw InvalidUserDataException(msg)
        }
        val file = matchingFiles.first()
        var docTests = extractAllDoctests(file) ?: return
        if (lineNumber != null) {
            docTests = docTests.filter { (lineNumber!! - 1) in it.lineNumbers }
        }
        if (docTests.isEmpty()) {
            var msg = "no doctests found in '$fileNameWithExt'"
            if (lineNumber != null) {
                msg += " on line $lineNumber"
            }
            logger.error(msg)
            throw InvalidUserDataException(msg)
        }
        docTests.forEach { docTest ->
            val lineNumbers = (docTest.lineNumbers.first + 1)..(docTest.lineNumbers.last + 1)
            val testFile = File(dir, generateFileName(docTest, file, lineNumbers))
            val testContent = """
                                |// generated from $file - lines $lineNumbers
                                |${docTest.content}
                            """.trimMargin()
            testFile.writeText(testContent)
            logger.info("  $testFile  ")
        }
    }

    private fun generateFileName(
        docTest: DocTest,
        file: File,
        lineNumbers: IntRange
    ) = "${docTest.pkg}.${file.nameWithoutExtension}.${lineNumbers.first}-${lineNumbers.last}.kt"

    private fun extractAllDoctests(file: File): List<DocTest>? {
        val text = file.readText()
        val pkg = extractPackage(text)
        val rawDocTests = extractAllRawDocTests(text)
        if (pkg == null) {
            logger.info("file $file - no package definition detected, skipping")
            return null
        }
        val docTests = rawDocTests.map {
            val name = "${
                pkg.replace(
                    '.',
                    '_'
                )
            }_${file.nameWithoutExtension}_${it.lineNumbers.first + 1}_${it.lineNumbers.last + 1}"
            generateDocTest(it, pkg, name)
        }
        if (docTests.isEmpty()) {
            logger.info("file $file - no doctests detected, skipping ")
            return null
        }
        logger.info("file $file detected ${docTests.size} doctests")
        return docTests
    }
}

package io.github.slava0135.doktest.extractor

fun extractPackage(text: String): String? {
    return text.split(" ", "\n").getOrNull(1)
}

data class RawDocTest(val content: List<String>, val lineNumbers: IntRange, val option: Option)

enum class Option {
    RUN,
    NORUN,
    NOMAIN,
}

val defaultOption = Option.RUN

fun extractAllRawDocTests(text: String): List<RawDocTest> {
    val result = mutableListOf<RawDocTest>()
    val docs = extractDocs(text)
    for (doc in docs) {
        result.addAll(extractRawDocTests(doc))
    }
    return result
}

private data class Doc(val content: List<String>, val lineNumbers: IntRange)

private fun extractDocs(text: String): List<Doc> {
    val indices = extractDocIndices(text)
    val lines = text.lines()
    val docs = indices.map {
        val content = extractDocContent(lines.slice(it))
        Doc(content, it)
    }
    return docs
}

const val DOC_START = "/**"
const val DOC_MID = "*"
const val DOC_END = "*/"

private fun extractDocIndices(text: String): List<IntRange> {
    val lines = text.lines()
    val result = mutableListOf<IntRange>()
    var startIndex = 0
    var offset = 0
    var isDoc = false
    for (lineNumber in lines.indices) {
        val line = lines[lineNumber]
        if (!isDoc) {
            if (line.trimStart().startsWith(DOC_START)) {
                isDoc = true
                startIndex = lineNumber
                offset = line.indexOf(DOC_MID)
            }
        } else {
            if (line.trimStart().startsWith(DOC_END)) {
                if (line.indexOf(DOC_MID) == offset) {
                    result.add(startIndex..lineNumber)
                }
                isDoc = false
                continue
            }
            isDoc = line.trimStart().startsWith(DOC_MID) && line.indexOf(DOC_MID) == offset
                    || line.isBlank()
        }
    }
    return result
}

private fun extractDocContent(lines: List<String>): List<String> {
    val first = lines.first().trimStart().removePrefix(DOC_START)
    val mid = lines.drop(1).dropLast(1).map {
        it.trimStart().removePrefix(DOC_MID).removePrefix(" ")
    }
    return listOf(first, *mid.toTypedArray())
}

const val DOCTEST_START = "```kotlin doctest"
const val OPTION_SEP = ":"
const val DOCTEST_END = "```"

private fun extractRawDocTests(doc: Doc): List<RawDocTest> {
    val result = mutableListOf<RawDocTest>()
    var isDocTest = false
    var startIndex = 0
    var option = defaultOption
    for ((relLineNumber, line) in doc.content.withIndex()) {
        if (!isDocTest) {
            if (!line.startsWith(DOCTEST_START)) {
                continue
            }
            isDocTest = true
            startIndex = relLineNumber
            option = defaultOption
            var lineTrim = line.removePrefix(DOCTEST_START)
            if (!lineTrim.startsWith(OPTION_SEP)) {
                continue
            }
            lineTrim = lineTrim.removePrefix(OPTION_SEP)
            val optionArgs = lineTrim.split(" ")
            if (optionArgs.isEmpty()) {
                continue
            }
            option = when (optionArgs.first().uppercase()) {
                Option.RUN.toString() -> Option.RUN
                Option.NORUN.toString() -> Option.NORUN
                Option.NOMAIN.toString() -> Option.NOMAIN
                else -> option
            }
        } else {
            if (!line.startsWith(DOCTEST_END)) {
                continue
            }
            val offset = doc.lineNumbers.first
            result.add(
                RawDocTest(
                    doc.content.slice(startIndex + 1 until relLineNumber),
                    startIndex + offset..relLineNumber + offset,
                    option
                )
            )
            isDocTest = false
        }
    }
    return result
}
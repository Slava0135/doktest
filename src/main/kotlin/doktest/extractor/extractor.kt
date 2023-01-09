package doktest.extractor

fun extractPackage(text: String): String? {
    return text.split(" ").getOrNull(1)
}

data class RawDocTest(val content: List<String>, val lineNumbers: IntRange)

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

private fun extractRawDocTests(doc: Doc): List<RawDocTest> {
    val result = mutableListOf<RawDocTest>()
    var isDocTest = false
    var startIndex = 0
    for ((relLineNumber, line) in doc.content.withIndex()) {
        if (!isDocTest) {
            if (line.startsWith("```kotlin doctest")) {
                isDocTest = true
                startIndex = relLineNumber
            }
        } else {
            if (line.startsWith("```")) {
                val offset = doc.lineNumbers.first
                result.add(
                    RawDocTest(
                        doc.content.slice(startIndex + 1 until relLineNumber),
                        startIndex + offset..relLineNumber + offset
                    )
                )
                isDocTest = false
            }
        }
    }
    return result
}
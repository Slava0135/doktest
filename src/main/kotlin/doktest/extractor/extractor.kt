package doktest.extractor

import doktest.extractor.helpers.Doc

fun extractPackage(text: String): String? {
    return text.split(" ").getOrNull(1)
}

data class RawDocTest(val content: List<String>, val lineNumbers: IntRange)

fun extractRawDocTests(doc: Doc): List<RawDocTest> {
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
                result.add(RawDocTest(
                    doc.content.slice(startIndex + 1 until relLineNumber),
                    startIndex + offset..relLineNumber + offset
                ))
                isDocTest = false
            }
        }
    }
    return result
}
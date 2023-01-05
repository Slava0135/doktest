package doktest.extractor

fun extractPackage(text: String): String? {
    return text.split(" ").getOrNull(1)
}

data class Doc(val content: List<String>, val lineNumbers: IntRange)

fun extractDocs(text: String): List<Doc> {
    val indices = extractDocIndices(text)
    val lines = text.lines()
    val docs = indices.map {
        val content = extractDocContent(lines.slice(it))
        Doc(content, it.first + 1 until it.last)
    }
    return docs
}

const val docStart = "/**"
const val docMid = "*"
const val docEnd = "*/"

fun extractDocIndices(text: String): List<IntRange> {
    val lines = text.lines()
    val result = mutableListOf<IntRange>()
    var startIndex = 0
    var offset = 0
    var isDoc = false
    for (lineNumber in lines.indices) {
        val line = lines[lineNumber]
        if (!isDoc) {
            if (line.trimStart().startsWith(docStart)) {
                isDoc = true
                startIndex = lineNumber
                offset = line.indexOf(docMid)
            }
        } else {
            if (line.trimStart().startsWith(docEnd)) {
                if (line.indexOf(docMid) == offset) {
                    result.add(startIndex..lineNumber)
                }
                isDoc = false
                continue
            }
            isDoc = line.trimStart().startsWith(docMid) && line.indexOf(docMid) == offset
                    || line.isBlank()
        }
    }
    return result
}

fun extractDocContent(lines: List<String>): List<String> {
    return lines.drop(1).dropLast(1).map {
        it.trimStart().removePrefix(docMid).removePrefix(" ")
    }
}

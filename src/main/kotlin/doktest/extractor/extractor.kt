package doktest.extractor

fun extractPackage(text: String): String? {
    return text.split(" ").getOrNull(1)
}

const val docStart = "/**"
const val docMid = "*"
const val docEnd = "*/"

data class Doc(val content: String, val lines: IntRange)

fun extractDocs(text: String): List<Doc> {
    val lines = text.lines()
    val result = mutableListOf<Doc>()
    var startIndex = 1
    var isDoc = false
    for (i in lines.indices) {
        val lineNumber = i + 1
        val line = lines[i]
        if (isDoc) {
            if (line.trimStart().startsWith(docEnd)) {
                val endIndex = lineNumber
                val content = lines.slice(startIndex - 1..endIndex - 1).joinToString("\n")
                result.add(Doc(content, startIndex..endIndex))
                continue
            }
            if (!line.trimStart().startsWith(docMid) && line.isNotBlank()) {
                isDoc = false
            }
        } else {
            if (line.trimStart().startsWith(docStart)) {
                startIndex = lineNumber
                isDoc = true
            }
        }
    }
    return result
}
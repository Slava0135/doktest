package doktest.extractor

import doktest.extractor.helpers.RawDocTest
import doktest.extractor.helpers.extractDocs
import doktest.extractor.helpers.extractRawDocTests

fun extractPackage(text: String): String? {
    return text.split(" ").getOrNull(1)
}

fun extractAllRawDocTests(text: String): List<RawDocTest> {
    val result = mutableListOf<RawDocTest>()
    val docs = extractDocs(text)
    for (doc in docs) {
        result.addAll(extractRawDocTests(doc))
    }
    return result
}
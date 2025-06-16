package com.mamboa.easyspans.compose

/**
 * Utility class for finding text ranges.
 */
object Utils {
    /**
     * Gets the ranges of all matches of the given regex in the text.
     */
    fun getRegexMatchRanges(text: String, regex: Regex): List<IntRange> {
        return regex.findAll(text).map { it.range }.toList()
    }

    /**
     * Gets the ranges of all words separated by the given delimiter.
     */
    fun getBoundaryRanges(text: String, delimiter: String): List<IntRange> {
        if (delimiter.isEmpty() || !text.contains(delimiter)) {
            return emptyList()
        }

        val ranges = mutableListOf<IntRange>()
        var startIndex = 0

        // Split the text by the delimiter and track the ranges
        text.split(delimiter).forEachIndexed { index, part ->
            if (part.isNotEmpty()) {
                val endIndex = startIndex + part.length - 1
                ranges.add(IntRange(startIndex, endIndex))
            }
            startIndex += part.length + delimiter.length
        }

        return ranges
    }
}

package com.mamboa.easyspans.compose

/**
 * Utility class for finding text ranges.
 */
object Utils {
    /**
     * Gets the ranges of occurrences based on the specified location and text.
     * It handles both boundary and regex delimitation types.
     *
     * @param location The occurrence location containing delimitation type and position.
     * @param text The text in which to find occurrences.
     * @return A list of IntRange representing the positions of occurrences in the text.
     */
    fun getOccurrenceRanges(location: OccurrenceLocation, text: String): List<IntRange> {
        val delim = location.delimitationType
        val position = location.occurrencePosition
        val ranges = when (delim) {
            is DelimitationType.Boundary -> getBoundaryRanges(text, delim.delimiter)
            is DelimitationType.Regex -> {
                try {
                    getRegexMatchRanges(text, delim.regex)
                } catch (e: Exception) {
                    // If regex is invalid, skip this chunk
                    emptyList()
                }
            }
        }
        return when (position) {
            is OccurrencePosition.All -> ranges
            is OccurrencePosition.First -> ranges.take(1)
            is OccurrencePosition.Last -> ranges.takeLast(1)
            is OccurrencePosition.Nth -> ranges.getOrNull(position.n)?.let { listOf(it) } ?: emptyList()
            is OccurrencePosition.Indices -> position.indices.mapNotNull { ranges.getOrNull(it) }
        }
    }

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

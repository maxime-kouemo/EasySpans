package com.mamboa.easyspans.legacy.helper

/**
 * Holds defining information for a text chunk's range/occurrence,
 * including how to delimit (match) and which occurrence(s) to operate on.
 *
 * @property delimitationType The way to slice/match the text (regex/boundary/none)
 * @property occurrencePosition Which occurrence(s) of the match are targeted (default: ALL)
 */
data class OccurrenceLocation(
    val delimitationType: DelimitationType,
    val occurrencePosition: OccurrencePosition = OccurrencePosition.All
)
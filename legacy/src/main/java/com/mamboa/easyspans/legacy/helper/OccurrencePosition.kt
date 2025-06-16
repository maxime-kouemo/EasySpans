package com.mamboa.easyspans.legacy.helper

/**
 * Represents the position(s) of an occurrence in text matching.
 * Used for specifying whether to span the first, nth, multiple, all, last, or a to-be-decided position.
 */
sealed class OccurrencePosition {
    /** The first matching occurrence. */
    data object First : OccurrencePosition()

    /** The nth matching occurrence (0-based index by default). */
    data class Nth(val n: Int = 0) : OccurrencePosition()

    /** Specific indexed occurrences, e.g. 0, 2, 5 means the 1st, 3rd, and 6th occurrences. */
    data class Indices(val indices: List<Int>) : OccurrencePosition() {
        constructor(vararg positions: Int) : this(positions.toList())
    }

    /** All matching occurrences. */
    data object All : OccurrencePosition()

    /** The last matching occurrence. */
    data object Last : OccurrencePosition()
}
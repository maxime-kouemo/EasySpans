package com.mamboa.easyspans.legacy.helper

/**
 * Indicates how a text chunk should be styled in EasySpans processing.
 *
 * - NO_LINK_COMMON: standard (non-link) styling applied to all such occurrences.
 * - LINK_COMMON: link styling applied to all such occurrences.
 * - INDEPENDENT(key): styling for an independently keyed, custom occurrence.
 */
sealed class OccurrenceType {
    /** Regular chunk: receives only non-link styles. */
    object NO_LINK_COMMON : OccurrenceType()

    /** Custom, per-occurrence styling keyed by [key]. */
    data class INDEPENDENT(val key: String) : OccurrenceType()
}
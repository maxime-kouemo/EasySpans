package com.mamboa.easyspans.legacy.styling

import android.text.SpannableStringBuilder
import com.mamboa.easyspans.legacy.helper.OccurrenceType

/**
 * Interface defining the contract for applying spans to text.
 * This interface provides a common abstraction that can be used
 * for both traditional Android spans and Compose-based span implementations.
 */
interface SpanApplier {
    /**
     * Prepares the spans that will be applied to text.
     */
    fun prepareSpans()

    /**
     * Applies spans to the specified subsequence of the SpannableStringBuilder.
     *
     * @param builder The SpannableStringBuilder to which the spans will be applied.
     * @param startIndex The starting index of the subsequence.
     * @param endIndex The ending index of the subsequence.
     * @param occurrenceType The type of occurrence for which to apply the spans.
     * @param boundaries Optional list of text boundaries for boundary matching.
     */
    fun applySpansToSequence(
        builder: SpannableStringBuilder,
        startIndex: Int,
        endIndex: Int,
        occurrenceType: OccurrenceType,
        boundaries: List<CharSequence> = emptyList()
    )

    /**
     * Cleans up any resources used during span application.
     */
    fun cleanup()
}
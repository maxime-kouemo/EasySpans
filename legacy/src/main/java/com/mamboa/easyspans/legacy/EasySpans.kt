package com.mamboa.easyspans.legacy

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.CharacterStyle
import android.text.style.ParagraphStyle
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.FontRes
import androidx.annotation.StyleRes
import com.mamboa.easyspans.legacy.customspans.TextCaseSpan
import com.mamboa.easyspans.legacy.helper.DelimitationType
import com.mamboa.easyspans.legacy.helper.OccurrenceChunk
import com.mamboa.easyspans.legacy.helper.OccurrenceLocation
import com.mamboa.easyspans.legacy.helper.OccurrencePosition
import com.mamboa.easyspans.legacy.helper.OccurrenceType
import com.mamboa.easyspans.legacy.helper.ScriptType
import com.mamboa.easyspans.legacy.helper.SequenceBackgroundColor
import com.mamboa.easyspans.legacy.styling.SpanFactory
import com.mamboa.easyspans.legacy.styling.StyleApplier
import java.util.ArrayList

/**
 * EasySpans provides a fluent API for applying rich text styling to Android text.
 * It allows for configuration of text styles, colors, backgrounds, and more,
 * with support for both global styles and specific occurrence chunks.
 * It is designed to work seamlessly with TextViews,
 * allowing for easy application of styles to text content.
 */
class EasySpans private constructor(
    private val context: Context,
    private var charSequence: CharSequence,
    private val targetTextView: TextView? = null,
    private val config: Config
) {
    private val spanFactory = SpanFactory(context)
    private val styleApplier = StyleApplier(config, context, targetTextView, spanFactory)

    private val boundaries = arrayListOf<CharSequence>()

    /**
     * Creates the final styled CharSequence by applying all configured spans.
     */
    fun create(): CharSequence {
        var currentText = SpannableStringBuilder(charSequence)

        styleApplier.prepareSpans()

        // First apply global spans if any exist in StyleApplier
        if (styleApplier.hasSpans()) {
            currentText = applyChangesAccordingToDelimitationType(
                currentText, // Pass the mutable builder
                config.occurrenceLocation,
                OccurrenceType.NO_LINK_COMMON
            )
        }

        // Then apply occurrence chunks if present
        if (config.occurrenceChunks.isNotEmpty()) {
            // Ensure applyChunksToText works on the same SpannableStringBuilder
            currentText = applyChunksToText(currentText) as SpannableStringBuilder
        }

        styleApplier.cleanup()
        return currentText
    }

    /**
     * Applies the occurrence chunks to the text based on their configuration.
     * Each chunk is processed according to its occurrence type and location.
     *
     * @param inputText The SpannableStringBuilder to which the chunks will be applied.
     * @return The modified SpannableStringBuilder with all chunks applied.
     */
    private fun applyChunksToText(inputText: SpannableStringBuilder): SpannableStringBuilder {
        var currentText = inputText
        config.occurrenceChunks.forEachIndexed { index, chunkType ->
            val occurrenceType =
                chunkType.getOccurrenceType(styleApplier.getOccurrenceChunkTag(index))
            val currentOccurrencePosition = chunkType.location.occurrencePosition

            currentText = applyChangesAccordingToDelimitationType(
                currentText,
                OccurrenceLocation(chunkType.location.delimitationType, currentOccurrencePosition),
                occurrenceType
            )
        }
        return currentText
    }

    /**
     * Slices the text based on the occurrence location and applies styles accordingly.
     * This method handles boundary delimiters.
     *
     * @param text The SpannableStringBuilder to be sliced and styled.
     * @param occurrenceLocation The location of the occurrence to be styled.
     * @param occurrenceType The type of occurrence for which to apply the styles.
     * @return A new SpannableStringBuilder with the applied styles.
     */
    private fun sliceTextOccurrencePositionsByBoundaryDelimiter(
        text: SpannableStringBuilder,
        occurrenceLocation: OccurrenceLocation,
        occurrenceType: OccurrenceType
    ): SpannableStringBuilder {
        val delimiter = occurrenceLocation.delimitationType as DelimitationType.BOUNDARY
        val occurrencePosition = occurrenceLocation.occurrencePosition

        // Work with a mutable copy if the input isn't already one
        val originalSpannable = SpannableStringBuilder(text)

        val boundaryValue = delimiter.value
        if (boundaryValue.isEmpty()) {
            // ... (handle empty boundary as before, apply to originalSpannable or a copy)
            return applyChangesToSequence(originalSpannable, occurrenceType)
        }

        val parts = originalSpannable.split(delimiter.compiledRegexPattern)
        boundaries.addAll(parts) // Assuming this is the desired behavior for `this.boundaries`

        var currentOffset = 0
        parts.forEachIndexed { index, subsequence ->
            val originalSubsequenceStart = findOriginalStart(
                originalText = originalSpannable.toString(),
                partText = subsequence,
                searchFrom = currentOffset,
                previousDelimiter = if (index > 0) boundaryValue else ""
            )
            val originalSubsequenceEnd = originalSubsequenceStart + subsequence.length

            val shouldApply = when (occurrencePosition) {
                is OccurrencePosition.First -> index == 0
                is OccurrencePosition.Nth -> index == occurrencePosition.n
                is OccurrencePosition.Indices -> occurrencePosition.indices.contains(index)
                is OccurrencePosition.Last -> index == parts.size - 1
                is OccurrencePosition.All -> true
            }

            if (shouldApply && subsequence.isNotBlank()) {
                // Apply changes directly on the originalSpannable within the correct range
                // This modifies originalSpannable in place.
                styleApplier.applySpansToSequence(
                    originalSpannable, // Apply to the main spannable
                    originalSubsequenceStart,
                    originalSubsequenceEnd,
                    occurrenceType,
                    this.boundaries
                )
            }

            // Advance offset for next search
            // This logic for currentOffset needs to be robust.
            // It's tricky because spans can change content for `indexOf`.
            // A safer way is to iterate with a regex matcher to find delimiter boundaries.
            if (originalSubsequenceStart != -1) {
                currentOffset = originalSubsequenceEnd + if (index < parts.size - 1) boundaryValue.length else 0
            } else {
                // Fallback if subsequence not found (shouldn't happen with split)
                currentOffset += subsequence.length + if (index < parts.size - 1) boundaryValue.length else 0
            }
        }
        return originalSpannable
    }

    /**
     * Finds the original start index of a part of text in the original text.
     * This function is designed to be used when text has been segmented by a delimiter,
     * and you need to find where a specific segment (`partText`) originally started
     * in the `originalText`, searching from a given `searchFrom` index.
     *
     * @param originalText The original, unmodified text.
     * @param partText The specific segment of text whose starting position is to be found.
     * @param searchFrom The index in `originalText` from which to begin the search for `partText`.
     *                   This is typically the end position of the previously found segment plus
     *                   the length of the delimiter that separated it from the current `partText`.
     * @param previousDelimiter The delimiter string that preceded this `partText`. This is used
     *                          to help accurately locate `partText` if `partText` itself might
     *                          appear earlier in `originalText` before the `searchFrom` point.
     *                          However, this parameter is NOT used in the current improved version
     *                          as `indexOf(string, startIndex)` is generally sufficient if `searchFrom`
     *                          is correctly managed.
     * @return The starting index of `partText` within `originalText` at or after `searchFrom`.
     *         Returns `searchFrom` if `partText` is not found (though this usually indicates
     *         an issue with how `partText` or `searchFrom` was derived).
     *         Returns -1 if `partText` is not found and `searchFrom` was beyond the text length
     *         or `partText` simply doesn't exist after `searchFrom`.
     */
    private fun findOriginalStart(
        originalText: String,
        partText: String,
        searchFrom: Int,
        @Suppress("UNUSED_PARAMETER") previousDelimiter: String // Marked unused as it's not strictly needed for indexOf
    ): Int {
        if (partText.isEmpty()) {
            // If the part we're looking for is empty, its "start" could be considered
            // the searchFrom position, as an empty string is technically found everywhere.
            // However, this often depends on the specific logic of the calling code.
            // For segmentation, an empty part usually means two delimiters were adjacent.
            return searchFrom.coerceAtMost(originalText.length)
        }

        if (searchFrom < 0 || searchFrom > originalText.length) {
            // If searchFrom is out of bounds, we can't find anything.
            return -1 // Or throw IllegalArgumentException, depending on desired strictness
        }

        // `originalText.indexOf(partText, searchFrom)` will search for the first
        // occurrence of `partText` starting at or after `searchFrom`.
        // This is generally what's needed if `searchFrom` is correctly calculated
        // (i.e., it's the position right after the previous delimiter).
        val foundIndex = originalText.indexOf(partText, startIndex = searchFrom)

        return if (foundIndex != -1) {
            foundIndex
        } else {
            // If partText is not found starting from searchFrom, it might indicate:
            // 1. The partText genuinely doesn't exist there.
            // 2. The originalText was modified in a way that partText changed (e.g. spans changing characters).
            //    This function assumes originalText is the pristine, initial text.
            // 3. searchFrom was miscalculated.
            //
            // Returning searchFrom as a fallback like in the original version can lead to
            // incorrect span applications if the part truly isn't at searchFrom.
            // Returning -1 is often safer to indicate "not found".
            // The calling code (sliceTextOccurrencePositionsByBoundaryDelimiter)
            // should handle a -1 return value appropriately (e.g., by logging an error
            // or skipping the part).
            -1 // Indicate not found
        }
    }

    /**
     * Slices the text based on the occurrence location and applies styles accordingly.
     * This method handles regex delimiters.
     *
     * @param text The SpannableStringBuilder to be sliced and styled.
     * @param occurrenceLocation The location of the occurrence to be styled.
     * @param occurrenceType The type of occurrence for which to apply the styles.
     * @return A new SpannableStringBuilder with the applied styles.
     */
    private fun sliceTextOccurrencePositionsByRegexDelimiter(
        text: SpannableStringBuilder,
        occurrenceLocation: OccurrenceLocation,
        occurrenceType: OccurrenceType
    ): SpannableStringBuilder {
        val delimiter = occurrenceLocation.delimitationType as DelimitationType.REGEX
        val occurrencePosition = occurrenceLocation.occurrencePosition
        return text.apply {
            val matcher = delimiter.compiledRegexPattern?.matcher(text) ?: return@apply
            when (occurrencePosition) {
                is OccurrencePosition.All -> {
                    while (matcher.find()) {
                        styleApplier.applySpansToSequence(
                            this, matcher.start(), matcher.end(),
                            occurrenceType, boundaries
                        )
                    }
                }

                is OccurrencePosition.First -> {
                    if (matcher.find()) {
                        styleApplier.applySpansToSequence(
                            this, matcher.start(), matcher.end(),
                            occurrenceType, boundaries
                        )
                    }
                }

                is OccurrencePosition.Nth -> {
                    var accumulator = 0
                    while (matcher.find()) {
                        if (occurrencePosition.n == accumulator) {
                            styleApplier.applySpansToSequence(
                                this, matcher.start(), matcher.end(),
                                occurrenceType, boundaries
                            )
                            break
                        } else {
                            accumulator++
                        }
                    }
                }

                is OccurrencePosition.Indices -> {
                    var accumulator = 0
                    var unVisitedItems = occurrencePosition.indices.size
                    while (matcher.find()) {
                        if (occurrencePosition.indices.contains(accumulator)) {
                            styleApplier.applySpansToSequence(
                                this, matcher.start(), matcher.end(),
                                occurrenceType, boundaries
                            )
                            unVisitedItems--
                            if (unVisitedItems == 0) {
                                break
                            }
                        }
                        accumulator++
                    }
                }

                is OccurrencePosition.Last -> {
                    var lastOccurrenceStart = -1
                    var lastOccurrenceEnd = -1
                    while (matcher.find()) {
                        lastOccurrenceStart = matcher.start()
                        lastOccurrenceEnd = matcher.end()
                    }
                    if (lastOccurrenceStart != -1 && lastOccurrenceEnd != -1) {
                        styleApplier.applySpansToSequence(
                            this, lastOccurrenceStart, lastOccurrenceEnd,
                            occurrenceType, boundaries
                        )
                    }
                }
            }
        }
    }

    /**
     * Applies the spans to the entire sequence of the SpannableStringBuilder.
     *
     * @param sequence The CharSequence to which the spans will be applied.
     * @param occurrenceType The type of occurrence for which to apply the spans.
     * @return A new CharSequence with the applied spans.
     */
    private fun applyChangesToSequence(
        sequence: SpannableStringBuilder,
        occurrenceType: OccurrenceType
    ): SpannableStringBuilder {
        return sequence.apply {
            if (isNotBlank()) {
                styleApplier.applySpansToSequence(
                    this,
                    0,
                    sequence.length,
                    occurrenceType,
                    boundaries
                )
            }
        }
    }

    /**
     * Applies changes to the text based on the occurrence location and type.
     * This method determines how to slice the text based on the delimitation type
     * and applies the appropriate styles.
     *
     * @param text The SpannableStringBuilder to be modified.
     * @param occurrenceLocation The location of the occurrence to be styled.
     * @param occurrenceType The type of occurrence for which to apply the styles.
     * @return A new SpannableStringBuilder with the applied styles.
     */
    private fun applyChangesAccordingToDelimitationType(
        text: SpannableStringBuilder,
        occurrenceLocation: OccurrenceLocation,
        occurrenceType: OccurrenceType
    ): SpannableStringBuilder {
        return when (occurrenceLocation.delimitationType) {
            is DelimitationType.REGEX -> sliceTextOccurrencePositionsByRegexDelimiter(
                text,
                occurrenceLocation,
                occurrenceType
            )
            is DelimitationType.BOUNDARY -> sliceTextOccurrencePositionsByBoundaryDelimiter(
                text,
                occurrenceLocation,
                occurrenceType
            )
            is DelimitationType.NONE -> applyChangesToSequence(text, occurrenceType)
        }
    }

    /**
     * Configuration class for EasySpans.
     * This class holds all the styling options that can be applied to the text.
     */
    data class Config(
        @ColorRes val color: Int = ID_NULL,
        @ColorRes val chunkBackgroundColor: Int = ID_NULL,
        @DimenRes val textSize: Int = ID_NULL,
        @StyleRes val style: Int = ID_NULL,
        @FontRes val font: Int = ID_NULL,
        val textStyle: Int = ID_UNSET,
        val isUnderlined: Boolean = false,
        val isStrikeThrough: Boolean = false,
        val scriptType: ScriptType = ScriptType.NONE,
        val paragraphBackgroundColor: SequenceBackgroundColor? = null,
        val textCaseType: TextCaseSpan.TextCaseType = TextCaseSpan.TextCaseType.NORMAL,
        val occurrenceChunks: Array<out OccurrenceChunk> = arrayOf(),
        val occurrenceLocation: OccurrenceLocation = OccurrenceLocation(
            DelimitationType.NONE,
            OccurrencePosition.All
        ),
        val customCharacterStyles: ArrayList<(Any) -> CharacterStyle>? = null,
        val customParagraphStyles: ArrayList<(Any) -> ParagraphStyle>? = null,
    ) {
        companion object {
            const val ID_NULL = 0
            const val ID_UNSET = -1
            internal const val COLOR_TAG = "color"
            internal const val TEXT_STYLE_TAG = "textStyle"
            internal const val TEXT_SIZE_TAG = "textSize"
            internal const val FONT_TAG = "textFont"
            internal const val STYLE_TAG = "style"
            internal const val UNDERLINED_TAG = "underlined"
            internal const val STRIKE_THROUGH_TAG = "strikeThrough"
            internal const val BACKGROUND_PARAGRAPH_TAG = "paragraph_background"
            internal const val BACKGROUND_CHUNK_TAG = "chunk_background"
            internal const val TEXT_CASE_TYPE_TAG = "textCaseType"
            internal const val CLICKABLE_LINK_TAG = "linkTs"
            internal const val SUPER_SCRIPT_TAG = "SuperscriptSpan"
            internal const val SUB_SCRIPT_TAG = "SubscriptSpan"
        }
    }

    /**
     * Builder class for EasySpans.
     * This class provides a fluent API for configuring the EasySpans instance.
     */
    class Builder(val context: Context, val charSequence: CharSequence, val targetTextView: TextView? = null) {
        private var color: Int = Config.ID_NULL
        private var chunkBackgroundColor: Int = Config.ID_NULL
        private var textSize: Int = Config.ID_NULL
        private var style: Int = Config.ID_NULL
        private var font: Int = Config.ID_NULL
        private var textStyle: Int = Config.ID_UNSET
        private var isUnderlined: Boolean = false
        private var isStrikeThrough: Boolean = false
        private var scriptType: ScriptType = ScriptType.NONE
        private var paragraphBackgroundColor: SequenceBackgroundColor? = null
        private var textCaseType: TextCaseSpan.TextCaseType = TextCaseSpan.TextCaseType.NORMAL
        private var occurrenceChunks: Array<out OccurrenceChunk> = arrayOf()
        private var occurrenceLocation: OccurrenceLocation =
            OccurrenceLocation(DelimitationType.NONE, OccurrencePosition.All)
        private var customCharacterStyles: ArrayList<(Any) -> CharacterStyle>? = null
        private var customParagraphStyles: ArrayList<(Any) -> ParagraphStyle>? = null

        fun setColor(@ColorRes value: Int) = apply { color = value }
        fun setChunkBackgroundColor(@ColorRes value: Int) = apply { chunkBackgroundColor = value }
        fun setTextStyle(value: Int) = apply { textStyle = value }
        fun setStyle(value: Int) = apply { style = value }
        fun setTextSize(@DimenRes value: Int) = apply { textSize = value }
        fun setFont(@FontRes value: Int) = apply { font = value }
        fun setParagraphBackgroundColor(value: SequenceBackgroundColor) =
            apply { paragraphBackgroundColor = value }

        fun isUnderlined() = apply { isUnderlined = true }
        fun isStrikeThrough() = apply { isStrikeThrough = true }
        fun setScriptType(value: ScriptType) = apply { scriptType = value }
        fun setTextCaseType(value: TextCaseSpan.TextCaseType) = apply { textCaseType = value }
        fun setOccurrenceChunks(vararg value: OccurrenceChunk) = apply {
            occurrenceChunks = value
        }

        fun setOccurrenceLocation(value: OccurrenceLocation) = apply { occurrenceLocation = value }

        private fun addACharacterSpan(style: (Any) -> CharacterStyle) = apply {
            if (customCharacterStyles == null) {
                customCharacterStyles = ArrayList()
            }
            customCharacterStyles?.add(style)
        }

        private fun addCustomParagraphStyle(style: (Any) -> ParagraphStyle) = apply {
            if (customParagraphStyles == null) {
                customParagraphStyles = ArrayList()
            }
            customParagraphStyles?.add(style)
        }

        fun addSpan(span: (Any) -> Any) = apply {
            when (val result = span(Any())) {
                is CharacterStyle -> addACharacterSpan { _ -> result }
                is ParagraphStyle -> addCustomParagraphStyle { _ -> result }
                else -> throw IllegalArgumentException("Span must produce a CharacterStyle or ParagraphStyle")
            }
        }

        fun build(): EasySpans {
            val config = Config(
                color = color,
                chunkBackgroundColor = chunkBackgroundColor,
                textSize = textSize,
                style = style,
                font = font,
                textStyle = textStyle,
                isUnderlined = isUnderlined,
                isStrikeThrough = isStrikeThrough,
                scriptType = scriptType,
                paragraphBackgroundColor = paragraphBackgroundColor,
                textCaseType = textCaseType,
                occurrenceChunks = occurrenceChunks,
                occurrenceLocation = occurrenceLocation,
                customCharacterStyles = customCharacterStyles,
                customParagraphStyles = customParagraphStyles
            )
            return EasySpans(context, charSequence, targetTextView, config)
        }
    }
}
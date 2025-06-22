package com.mamboa.easyspans.legacy.styling

import android.content.Context
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.text.style.ParagraphStyle
import android.text.style.URLSpan
import android.util.Log
import android.widget.TextView
import com.mamboa.easyspans.legacy.EasySpans
import com.mamboa.easyspans.legacy.customspans.ClickableLinkSpan
import com.mamboa.easyspans.legacy.customspans.TextCaseSpan
import com.mamboa.easyspans.legacy.helper.DelimitationType
import com.mamboa.easyspans.legacy.helper.OccurrenceChunk
import com.mamboa.easyspans.legacy.helper.OccurrenceType
import com.mamboa.easyspans.legacy.helper.ScriptType
import com.mamboa.easyspans.legacy.styling.SpanFactory.Companion.setCustomSpan
import java.util.UUID

class StyleApplier(
    private val config: EasySpans.Config,
    private val context: Context,
    private val targetTextView: TextView? = null,
    private val spanFactory: SpanFactory
) : SpanApplier {

    private val mapOfCharacterStyleSpans = linkedMapOf<String, (Any) -> CharacterStyle>()
    private val mapOfParagraphStyleSpans = linkedMapOf<String, (Any) -> ParagraphStyle?>()
    private val occurrenceChunksTags = arrayListOf<String>()
    private val occurrenceChunksDetails = arrayListOf<OccurrenceChunk>()
    private val builtChunks = hashSetOf<Int>() // Track built chunks by index

    /**
     * Prepares the spans based on the provided configuration.
     * This method sets up the base spans and occurrence chunks that will be applied later.
     */
    override fun prepareSpans() {
        setupBaseSpans(config)
        setupOccurrenceChunks(config.occurrenceChunks)
    }

    /**
     * Returns the tag for the occurrence chunk at the specified index.
     * If the index is out of bounds, it returns an empty string.
     *
     * @param index The index of the occurrence chunk tag to retrieve.
     * @return The tag for the occurrence chunk, or an empty string if the index is invalid.
     */
    fun getOccurrenceChunkTag(index: Int): String {
        return if (index < occurrenceChunksTags.size) {
            occurrenceChunksTags[index]
        } else {
            ""
        }
    }

    /**
     * Checks if there are any spans to apply.
     * This is useful to avoid unnecessary processing when no styles are defined.
     *
     * @return true if there are spans, false otherwise.
     */
    fun hasSpans(): Boolean {
        return mapOfCharacterStyleSpans.isNotEmpty() || mapOfParagraphStyleSpans.isNotEmpty() ||
                occurrenceChunksTags.isNotEmpty() || occurrenceChunksDetails.isNotEmpty()
    }

    /**
     * Sets up the base spans based on the provided configuration.
     * It populates the maps with character and paragraph style spans.
     *
     * @param config The configuration containing styling options.
     */
    private fun setupBaseSpans(config: EasySpans.Config) {
        if (config.textCaseType != TextCaseSpan.TextCaseType.NORMAL && spanFactory.authorizedTextCases.contains(
                config.textCaseType
            )
        ) {
            mapOfCharacterStyleSpans[EasySpans.Config.TEXT_CASE_TYPE_TAG] =
                { spanFactory.createTextCaseSpan(config.textCaseType) }
        }
        if (config.textStyle != EasySpans.Config.ID_UNSET) {
            mapOfCharacterStyleSpans[EasySpans.Config.TEXT_STYLE_TAG] =
                { spanFactory.createStyleSpan(config.textStyle) }
        }
        if (config.style != EasySpans.Config.ID_NULL) {
            mapOfCharacterStyleSpans[EasySpans.Config.STYLE_TAG] =
                { spanFactory.createTextAppearanceSpan(config.style) }
        }
        if (config.color != EasySpans.Config.ID_NULL) {
            mapOfCharacterStyleSpans[EasySpans.Config.COLOR_TAG] =
                { spanFactory.createForegroundColorSpan(config.color) }
        }
        if (config.chunkBackgroundColor != EasySpans.Config.ID_NULL) {
            mapOfCharacterStyleSpans[EasySpans.Config.BACKGROUND_CHUNK_TAG] =
                { spanFactory.createBackgroundColorSpan(config.chunkBackgroundColor) }
        }
        if (config.textSize != EasySpans.Config.ID_NULL) {
            mapOfCharacterStyleSpans[EasySpans.Config.TEXT_SIZE_TAG] =
                { spanFactory.createTextSizeSpan(config.textSize) }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && config.font != EasySpans.Config.ID_NULL) {
            mapOfCharacterStyleSpans[EasySpans.Config.FONT_TAG] =
                { spanFactory.createFontSpan(config.font, config.textStyle) }
        }
        if (config.isUnderlined) {
            mapOfCharacterStyleSpans[EasySpans.Config.UNDERLINED_TAG] =
                { spanFactory.createUnderlineSpan() }
        }
        if (config.isStrikeThrough) {
            mapOfCharacterStyleSpans[EasySpans.Config.STRIKE_THROUGH_TAG] =
                { spanFactory.createStrikeThroughSpan() }
        }
        when (config.scriptType) {
            ScriptType.SUPER -> mapOfCharacterStyleSpans[EasySpans.Config.SUPER_SCRIPT_TAG] =
                { spanFactory.createSuperScriptCharStyle() }

            ScriptType.SUB -> mapOfCharacterStyleSpans[EasySpans.Config.SUB_SCRIPT_TAG] =
                { spanFactory.createSubScriptCharStyle() }

            else -> {}
        }
        config.paragraphBackgroundColor?.let { bg ->
            mapOfParagraphStyleSpans[EasySpans.Config.BACKGROUND_PARAGRAPH_TAG] = {
                spanFactory.createBackgroundParagraphStyle(bg, targetTextView)
            }
        }
        config.customCharacterStyles?.forEach { value ->
            val sampleSpan = try {
                value(Any())
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid custom character style: ${e.message}")
            }
            if (sampleSpan is ClickableLinkSpan || sampleSpan is ClickableSpan || sampleSpan is URLSpan) {
                throw IllegalArgumentException("Custom character styles cannot include ClickableLinkSpan or URLSpan. Use OccurrenceChunkBuilder.setOnLinkClickListener for clickable behavior.")
            }
            val key = UUID.randomUUID().toString()
            mapOfCharacterStyleSpans[key] = value
        }
        config.customParagraphStyles?.forEach { value ->
            val key = UUID.randomUUID().toString()
            mapOfParagraphStyleSpans[key] = value
        }
    }

    /**
     * Sets up the occurrence chunks by adding their tags and details to the respective lists.
     * It also builds each chunk once during setup to avoid repeated building later.
     *
     * @param chunks The array of occurrence chunks to set up.
     */
    private fun setupOccurrenceChunks(chunks: Array<out OccurrenceChunk>) {
        if (chunks.isEmpty()) return
        occurrenceChunksTags.ensureCapacity(chunks.size * 2)
        occurrenceChunksDetails.ensureCapacity(chunks.size * 2)
        chunks.forEachIndexed { index, span ->
            val key = "${EasySpans.Config.CLICKABLE_LINK_TAG}$index"
            occurrenceChunksTags.add(key)
            occurrenceChunksDetails.add(span)
            // Build chunk once during setup
            span.build(context, targetTextView)
            builtChunks.add(index)
        }
    }

    override fun applySpansToSequence(
        builder: SpannableStringBuilder,
        startIndex: Int,
        endIndex: Int,
        occurrenceType: OccurrenceType,
        boundaries: List<CharSequence>
    ) {
        val subsequence = builder.subSequence(startIndex, endIndex)
        when (occurrenceType) {
            is OccurrenceType.NO_LINK_COMMON -> {
                mapOfCharacterStyleSpans.forEach { (key, value) ->
                    builder.setCustomSpan(key, value(key), startIndex, endIndex)
                }
                mapOfParagraphStyleSpans.forEach { (key, value) ->
                    builder.setSpan(value(key), startIndex, endIndex, 0)
                }
            }
            is OccurrenceType.INDEPENDENT -> {
                if (occurrenceChunksTags.contains(occurrenceType.key)) {
                    val position = occurrenceChunksTags.indexOf(occurrenceType.key)
                    val isValidRegexPosition = isLinkValidRegexPosition(subsequence, position)
                    val isValidBoundaryPosition = isLinkValidBoundaryPosition(subsequence, position, boundaries)
                    if (isValidBoundaryPosition || isValidRegexPosition) {
                        val currentLinkDetails = occurrenceChunksDetails[position]
                        // Build was called in setupOccurrenceChunks
                        currentLinkDetails.characterStyleSpans.forEach { (key, value) ->
                            builder.setCustomSpan(key, value(key), startIndex, endIndex)
                        }
                        currentLinkDetails.paragraphStyleSpans.forEach { (key, value) ->
                            builder.setSpan(value(key), startIndex, endIndex, 0)
                        }
                    }
                }
            }
        }
    }

    private fun isLinkValidRegexPosition(subsequence: CharSequence, position: Int): Boolean {
        if (occurrenceChunksDetails.size <= position) return false
        val chunk = occurrenceChunksDetails[position]
        if (chunk.location.delimitationType !is DelimitationType.REGEX) return false
        val regex = (chunk.location.delimitationType as DelimitationType.REGEX).regex
        val isValid = regex?.find(subsequence) != null
        if (!isValid) {
            Log.d("EasySpans", "Regex ${regex?.pattern} did not match subsequence: $subsequence")
        }
        return isValid
    }

    private fun isLinkValidBoundaryPosition(
        subsequence: CharSequence,
        position: Int,
        boundaries: List<CharSequence>
    ): Boolean {
        return occurrenceChunksDetails.size > position &&
                occurrenceChunksDetails[position].location.delimitationType is DelimitationType.BOUNDARY &&
                boundaries.contains(subsequence.toString())
    }

    override fun cleanup() {
        occurrenceChunksDetails.forEach { it.cleanUp() }
        occurrenceChunksDetails.clear()
        occurrenceChunksTags.clear()
        mapOfParagraphStyleSpans.clear()
        mapOfCharacterStyleSpans.clear()
        builtChunks.clear()
    }
}
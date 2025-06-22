package com.mamboa.easyspans.compose

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformSpanStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.unit.TextUnit

/**
 * Represents the position of occurrences in the text.
 * It can be the first occurrence, last occurrence, all occurrences,
 * a specific nth occurrence, or a list of indices.
 */
@Stable
sealed class OccurrencePosition {
    data object First : OccurrencePosition()
    data object Last : OccurrencePosition()
    data object All : OccurrencePosition()
    data class Nth(val n: Int) : OccurrencePosition()
    data class Indices(val indices: List<Int>) : OccurrencePosition() {
        constructor(vararg positions: Int) : this(positions.toList())
    }
}

/**
 * Represents the type of delimitation used to find occurrences in the text.
 * It can be a simple boundary string or a regex pattern.
 */
@Stable
sealed class DelimitationType {
    data class Boundary(val delimiter: String) : DelimitationType()
    data class Regex(
        val pattern: String,
        val regex: kotlin.text.Regex = kotlin.text.Regex(pattern)
    ) : DelimitationType() {
        constructor(pattern: String, options: Set<RegexOption>) : this(
            pattern,
            kotlin.text.Regex(pattern, options)
        )
        constructor(pattern: String, dotMatchesAll: RegexOption) : this(
            pattern = pattern,
            regex = kotlin.text.Regex(pattern, setOf(dotMatchesAll))
        )
    }
}

/**
 * Represents the location of occurrences in the text.
 * It includes the type of delimitation and the position of occurrences.
 * For example, it can specify that occurrences are found by a regex pattern
 * and should include all occurrences, only the first occurrence, etc.
 *
 * @property delimitationType The type of delimitation used to find occurrences.
 * @property occurrencePosition The position of occurrences in the text.
 */
@Immutable
data class OccurrenceLocation(
    val delimitationType: DelimitationType,
    val occurrencePosition: OccurrencePosition = OccurrencePosition.All
)

/**
 * Represents a chunk of text with specific styling and behavior.
 * It includes the location of occurrences, an optional click tag,
 * a style builder function to apply styles, and an optional text transformation function.
 *
 * @property occurrenceLocation The location of occurrences in the text.
 * @property onClickTag An optional tag for click events.
 * @property styleBuilder A function to build the SpanStyle for this chunk.
 * @property textTransform An optional function to transform the text of this chunk.
 */
@Immutable
data class OccurrenceChunk(
    val occurrenceLocation: OccurrenceLocation,
    val onClickTag: String? = null,
    val styleBuilder: (SpanStyle) -> SpanStyle = { it },
    val textTransform: ((String) -> String)? = null
)

/**
 * Represents the type of script to be applied to the text.
 * It can be superscript, subscript, or none.
 */
enum class ScriptType {
    SUPER, SUB, NONE
}

/**
 * A builder for creating an AnnotatedString with easy spans in Compose.
 * This builder allows you to set various text styles and behaviors
 * such as color, background color, font size, weight, style, family,
 * text decoration, and occurrence locations.
 */
@Stable
class EasySpansComposeBuilder private constructor(
    private val text: String,
    private var spanStyle: SpanStyle = SpanStyle()
) {
    private var color: Color? = null
    private var backgroundColor: Color? = null
    private var fontSize: TextUnit? = null
    private var fontWeight: FontWeight? = null
    private var fontStyle: FontStyle? = null
    private var fontFamily: FontFamily? = null
    private var textDecoration: TextDecoration? = null
    private var textCase: ((String) -> String)? = null
    private var scriptType: ScriptType = ScriptType.NONE
    private var brush: Brush? = null
    private var alpha: Float = 1.0f
    private var fontSynthesis: FontSynthesis? = null
    private var fontFeatureSettings: String? = null
    private var letterSpacing: TextUnit? = null
    private var baselineShift: BaselineShift? = null
    private var textGeometricTransform: TextGeometricTransform? = null
    private var localeList: LocaleList? = null
    private var shadow: Shadow? = null
    private var platformStyle: PlatformSpanStyle? = null
    private var drawStyle: DrawStyle? = null

    private val occurrenceChunks: MutableList<OccurrenceChunk> = mutableListOf()
    private var occLocation: OccurrenceLocation? = null
    private val spanStyles = mutableListOf<SpanBuilder>()
    private val clickTags = mutableMapOf<String, Int>()
    private var baseStyle = SpanStyle()

    companion object {
        fun create(text: String): EasySpansComposeBuilder = EasySpansComposeBuilder(text)
    }

    fun setColor(color: Color) = apply { this.color = color }
    fun setBackgroundColor(color: Color) = apply { this.backgroundColor = color }
    fun setFontSize(size: TextUnit) = apply { this.fontSize = size }
    fun setFontWeight(weight: FontWeight) = apply { this.fontWeight = weight }
    fun setFontStyle(style: FontStyle) = apply { this.fontStyle = style }
    fun setFontFamily(family: FontFamily) = apply { this.fontFamily = family }
    fun setTextDecoration(decoration: TextDecoration?) = apply { this.textDecoration = decoration }
    fun setBrush(brush: Brush) = apply { this.brush = brush }
    fun setAlpha(alpha: Float) = apply { this.alpha = alpha }
    fun setFontSynthesis(synthesis: FontSynthesis) = apply { this.fontSynthesis = synthesis }
    fun setFontFeatureSettings(settings: String) = apply { this.fontFeatureSettings = settings }
    fun setLetterSpacing(spacing: TextUnit) = apply { this.letterSpacing = spacing }
    fun setBaselineShift(shift: BaselineShift) = apply { this.baselineShift = shift }
    fun setTextGeometricTransform(transform: TextGeometricTransform) =
        apply { this.textGeometricTransform = transform }

    fun setLocaleList(localeList: LocaleList) = apply { this.localeList = localeList }
    fun setShadow(shadow: Shadow) = apply { this.shadow = shadow }
    fun setPlatformStyle(style: PlatformSpanStyle) = apply { this.platformStyle = style }
    fun setDrawStyle(style: DrawStyle) = apply { this.drawStyle = style }
    fun setTextCase(case: (String) -> String) = apply { this.textCase = case }
    fun setOccurrenceLocation(location: OccurrenceLocation) = apply { occLocation = location }
    fun addOccurrenceChunk(chunk: OccurrenceChunk) = apply { occurrenceChunks.add(chunk) }
    fun setScriptType(scriptType: ScriptType) = apply { this.scriptType = scriptType }
    fun setOccurrenceChunks(vararg chunks: OccurrenceChunk) = apply {
        occurrenceChunks.clear()
        occurrenceChunks.addAll(chunks)
    }

    fun setSpanStyle(spanStyle: SpanStyle) = apply {
        this.spanStyle = spanStyle
        // If a span style is set, it will override the global style
        if (spanStyle != SpanStyle()) {
            this.color = null
            this.backgroundColor = null
            this.fontSize = null
            this.fontWeight = null
            this.fontStyle = null
            this.fontFamily = null
            this.textDecoration = null
            this.brush = null
            this.alpha = 1.0f
            this.fontSynthesis = null
            this.fontFeatureSettings = null
            this.letterSpacing = null
            this.textGeometricTransform = null
            this.localeList = null
            this.shadow = null
            this.platformStyle = null
            this.drawStyle = null
            this.baselineShift = null
            this.scriptType = ScriptType.NONE
        }
    }

    /**
     * Builds the global SpanStyle based on the properties set in the builder.
     *
     * @return A SpanStyle that can be applied to the entire text.
     */
    private fun buildGlobalSpanStyle(): SpanStyle {
        // Individual properties override the base spanStyle
        return spanStyle.merge(
            SpanStyle(
                color = color ?: Color.Unspecified,
                background = backgroundColor ?: Color.Unspecified,
                fontSize = fontSize ?: TextUnit.Unspecified,
                fontWeight = fontWeight,
                fontStyle = fontStyle,
                fontFamily = fontFamily,
                textDecoration = textDecoration,
                fontSynthesis = fontSynthesis ?: FontSynthesis.All,
                fontFeatureSettings = fontFeatureSettings ?: "",
                letterSpacing = letterSpacing ?: TextUnit.Unspecified,
                textGeometricTransform = textGeometricTransform ?: TextGeometricTransform(),
                localeList = localeList ?: LocaleList.current,
                shadow = shadow ?: Shadow(),
                platformStyle = platformStyle ?: PlatformSpanStyle(),
                drawStyle = drawStyle,
                baselineShift = when (scriptType) {
                    ScriptType.SUPER -> BaselineShift.Superscript
                    ScriptType.SUB -> BaselineShift.Subscript
                    ScriptType.NONE -> BaselineShift.None
                }
            ).copy(
                brush = brush ?: spanStyle.brush,
                alpha = alpha.takeIf { it != 1.0f } ?: spanStyle.alpha,
            )
        )
    }

    /**
     * Adds a span style to the builder.
     *
     * @param start The starting index of the span.
     * @param end The ending index of the span (exclusive).
     * @param styleBuilder A function that builds the SpanStyle based on the base style.
     * @param clickTag An optional tag for click events associated with this span.
     */
    private fun addSpanStyle(
        start: Int,
        end: Int,
        styleBuilder: (SpanStyle) -> SpanStyle,
        clickTag: String? = null
    ) {
        val spanBuilder = SpanBuilder(start, end, styleBuilder, clickTag)
        spanStyles.add(spanBuilder)
        clickTag?.let { tag ->
            clickTags[tag] = spanStyles.size - 1
        }
    }

    /**
     * Builds the AnnotatedString with the applied styles and spans.
     * It processes the occurrence chunks, applies styles, and handles text transformations.
     *
     * @return An AnnotatedString with the applied styles and spans.
     */
    fun build(): AnnotatedString {
        var displayText = textCase?.invoke(this.text) ?: this.text

        if (displayText.isEmpty()) return AnnotatedString("")

        baseStyle = buildGlobalSpanStyle()
        val processedOccurrenceChunks = occurrenceChunks.map { chunk ->
            if (textCase != null && chunk.occurrenceLocation.delimitationType is DelimitationType.Regex) {
                val originalRegexDelimitation = chunk.occurrenceLocation.delimitationType
                val updatedRegex = Regex(originalRegexDelimitation.pattern, RegexOption.IGNORE_CASE)
                chunk.copy(
                    occurrenceLocation = chunk.occurrenceLocation.copy(
                        delimitationType = DelimitationType.Regex(
                            originalRegexDelimitation.pattern,
                            updatedRegex
                        )
                    )
                )
            } else {
                chunk
            }
        }

        // Track annotations to ensure each range has at most one click tag
        val existingAnnotations = mutableMapOf<IntRange, String>()

        // Clear spanStyles and clickTags to avoid stale data
        spanStyles.clear()
        clickTags.clear()

        // Process occLocation if present
        occLocation?.let { loc ->
            var finalLoc = loc
            if (textCase != null && loc.delimitationType is DelimitationType.Regex) {
                val originalRegexDelimitation = loc.delimitationType
                val updatedRegex = Regex(originalRegexDelimitation.pattern, RegexOption.IGNORE_CASE)
                finalLoc = loc.copy(
                    delimitationType = DelimitationType.Regex(
                        originalRegexDelimitation.pattern,
                        updatedRegex
                    )
                )
            }
            val baseChunkForOccLocation = OccurrenceChunk(finalLoc)
            processedOccurrenceChunks.toMutableList().apply { add(baseChunkForOccLocation) }
        }

        // Collect transformations to apply to displayText
        val textTransformations = mutableListOf<Triple<Int, Int, String>>()

        // Add styles and annotations for each chunk
        for (chunk in processedOccurrenceChunks) {
            val ranges = Utils.getOccurrenceRanges(chunk.occurrenceLocation, displayText)
            for (range in ranges) {
                val intRange = range.first until (range.last + 1)
                // Add style and annotation if the range is not already annotated with a different tag
                if (chunk.onClickTag == null || existingAnnotations[intRange]?.let { it == chunk.onClickTag } != false) {
                    addSpanStyle(
                        start = range.first,
                        end = range.last + 1,
                        styleBuilder = chunk.styleBuilder,
                        clickTag = chunk.onClickTag
                    )

                    // Update annotation, allowing later chunks to override earlier ones
                    if (chunk.onClickTag != null) {
                        existingAnnotations[intRange] = chunk.onClickTag
                    }

                    // Store text transformation when available
                    chunk.textTransform?.let { transform ->
                        val substring = displayText.substring(range.first, range.last + 1)
                        val transformedText = transform(substring)
                        textTransformations.add(
                            Triple(
                                range.first,
                                range.last + 1,
                                transformedText
                            )
                        )
                    }
                }
            }
        }

        if (textTransformations.isNotEmpty()) {
            val sortedTransformations = textTransformations.sortedBy { it.first }
            val newText = StringBuilder(displayText)
            var offset = 0
            for ((start, end, transformedText) in sortedTransformations) {
                val adjustedStart = start + offset
                val adjustedEnd = start + offset + (end - start)
                newText.replace(adjustedStart, adjustedEnd, transformedText)
                offset += transformedText.length - (adjustedEnd - adjustedStart)
            }
            displayText = newText.toString()
        }

        return buildAnnotatedString {
            append(displayText)
            if (baseStyle != SpanStyle()) {
                addStyle(baseStyle, 0, displayText.length)
            }
            // Sort spanStyles by start index to ensure correct order
            //spanStyles.sortedBy { it.start }.forEach { spanBuilder ->
            spanStyles.sortedWith(compareBy({ it.start }, { -spanStyles.indexOf(it) }))
                .forEach { spanBuilder ->
                    val start = spanBuilder.start.coerceIn(0, displayText.length)
                    val end = spanBuilder.end.coerceIn(0, displayText.length)
                    if (start < end) {
                        val style = spanBuilder.styleBuilder(baseStyle)
                        addStyle(style, start, end)
                        spanBuilder.clickTag?.let { tag ->
                            val intRange = start until end
                            // Only add annotation if this is the intended tag for the range
                            if (existingAnnotations[intRange] == tag) {
                                addStringAnnotation(
                                    tag = tag,
                                    annotation = displayText.substring(start, end),
                                    start = start,
                                    end = end
                                )
                            }
                        }
                    }
                }
        }.also {
            spanStyles.clear()
            clickTags.clear()
            baseStyle = SpanStyle()
        }
    }

    /**
     * Adds a span style to the builder with the specified start and end indices,
     * style builder function, and an optional click tag.
     *
     * @param start The starting index of the span.
     * @param end The ending index of the span (exclusive).
     * @param styleBuilder A function that builds the SpanStyle based on the base style.
     * @param clickTag An optional tag for click events associated with this span.
     */
    private data class SpanBuilder(
        val start: Int,
        val end: Int,
        val styleBuilder: (SpanStyle) -> SpanStyle,
        val clickTag: String? = null
    )
}

/**
 * Creates an AnnotatedString with easy spans using the provided text and builder.
 *
 * @param text The text to be styled.
 * @param builder A lambda to configure the EasySpansComposeBuilder.
 * @return An AnnotatedString with the applied styles and spans.
 */
fun EasySpansCompose(
    text: String,
    builder: EasySpansComposeBuilder.() -> Unit
): AnnotatedString = EasySpansComposeBuilder.create(text).apply(builder).build()

fun occurrenceChunk(
    occurrenceLocation: OccurrenceLocation,
    onClickTag: String? = null,
    styleBuilder: (SpanStyle) -> SpanStyle = { it },
    textTransform: ((String) -> String)? = null
) = OccurrenceChunk(occurrenceLocation, onClickTag, styleBuilder, textTransform)

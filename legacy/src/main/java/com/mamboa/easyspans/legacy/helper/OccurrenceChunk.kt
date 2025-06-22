package com.mamboa.easyspans.legacy.helper

import android.content.Context
import android.os.Build
import android.text.style.CharacterStyle
import android.text.style.ParagraphStyle
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.widget.TextView
import com.mamboa.easyspans.legacy.EasySpans
import com.mamboa.easyspans.legacy.customspans.TextCaseSpan
import com.mamboa.easyspans.legacy.styling.SpanFactory
import java.util.UUID

/**
 * Represents a rich occurrence styling chunk, with configuration for spans and link handling.
 * Requires targetTextView at construction, ensuring no lateinit or null issues.
 */
class OccurrenceChunk(
    val location: OccurrenceLocation,
    val builder: OccurrenceChunkBuilder? = null
) {
    init {
        if (builder?.onLinkClickListener != null && location.delimitationType == DelimitationType.NONE) {
            println("Warning: Clickable chunk with DelimitationType.NONE may apply to entire text")
        }
    }

    // Encapsulate mutability, expose as read-only views
    private val _characterStyleSpans = hashMapOf<String, (Any) -> CharacterStyle>()
    val characterStyleSpans: Map<String, (Any) -> CharacterStyle> get() = _characterStyleSpans

    private val _paragraphStyleSpans = hashMapOf<String, (Any) -> ParagraphStyle?>()
    val paragraphStyleSpans: Map<String, (Any) -> ParagraphStyle?> get() = _paragraphStyleSpans

    fun getOccurrenceType(key: String): OccurrenceType {
        return if (builder == null)
            OccurrenceType.NO_LINK_COMMON
        else
            OccurrenceType.INDEPENDENT(key)
    }

    /**
     * Builds the occurrence chunk by applying the styles and spans defined in the builder.
     * @param context the context used to access resources
     * @param targetTextView the TextView to which the styles will be applied
     */
    fun build(context: Context, targetTextView: TextView?) {

        // Create a SpanFactory to generate spans
        val spanFactory = SpanFactory(context)

        builder?.run {
            setTargetTextView(targetTextView)

            if (textCaseType != TextCaseSpan.TextCaseType.NORMAL) {
                _characterStyleSpans[EasySpans.Config.TEXT_CASE_TYPE_TAG] =
                    { spanFactory.createTextCaseSpan(textCaseType) }
            }

            if (isStrikeThrough) {
                _characterStyleSpans[EasySpans.Config.STRIKE_THROUGH_TAG] =
                    { spanFactory.createStrikeThroughSpan() }
            }

            if (scriptType != ScriptType.NONE) {
                when (scriptType) {
                    ScriptType.SUPER -> {
                        _characterStyleSpans[EasySpans.Config.SUPER_SCRIPT_TAG] =
                            { SuperscriptSpan() }
                    }

                    ScriptType.SUB -> {
                        _characterStyleSpans[EasySpans.Config.SUB_SCRIPT_TAG] =
                            { SubscriptSpan() }
                    }

                    else -> {}
                }
            }

            if (color != EasySpans.Config.ID_NULL && !_characterStyleSpans.containsKey(EasySpans.Config.CLICKABLE_LINK_TAG)) {
                _characterStyleSpans[EasySpans.Config.COLOR_TAG] =
                    { spanFactory.createForegroundColorSpan(color) }
            }

            if (textSize != EasySpans.Config.ID_NULL) {
                _characterStyleSpans[EasySpans.Config.TEXT_SIZE_TAG] =
                    { spanFactory.createTextSizeSpan(textSize) }
            }

            if (textStyle != EasySpans.Config.ID_UNSET) {
                _characterStyleSpans[EasySpans.Config.TEXT_STYLE_TAG] =
                    { spanFactory.createStyleSpan(textStyle) }
            }

            if (style != EasySpans.Config.ID_NULL) {
                _characterStyleSpans[EasySpans.Config.STYLE_TAG] =
                    { spanFactory.createTextAppearanceSpan(style) }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && font != EasySpans.Config.ID_NULL) {
                _characterStyleSpans[EasySpans.Config.FONT_TAG] =
                    { spanFactory.createFontSpan(font, textStyle) }
            }


            if (isUnderlined && !_characterStyleSpans.containsKey(EasySpans.Config.CLICKABLE_LINK_TAG)) {
                _characterStyleSpans[EasySpans.Config.UNDERLINED_TAG] =
                    { spanFactory.createUnderlineSpan() }
            }

            if (chunkBackgroundColor != EasySpans.Config.ID_NULL) {
                _characterStyleSpans[EasySpans.Config.BACKGROUND_CHUNK_TAG] =
                    { spanFactory.createBackgroundColorSpan(chunkBackgroundColor) }
            }

            paragraphBackgroundColor?.let { backgroundColor ->
                targetTextView?.let { textView ->
                    _paragraphStyleSpans[EasySpans.Config.BACKGROUND_PARAGRAPH_TAG] =
                        { spanFactory.createBackgroundParagraphStyle(backgroundColor, textView) }
                } ?: throw NullPointerException(
                    "targetTextView must be provided when paragraphBackgroundColor is set"
                )
            }

            onLinkClickListener?.let { listener ->
                targetTextView?.let { textView ->
                    _characterStyleSpans[EasySpans.Config.CLICKABLE_LINK_TAG] = {
                        spanFactory.createClickableLinkSpan(
                            textView,
                            color,
                            isUnderlined,
                            listener
                        )
                    }
                } ?: throw NullPointerException(
                    "targetTextView must be provided when onLinkClickListener is set"
                )
            }

            // Only remove conflicting color if ClickableLinkSpan uses its own color
            if (_characterStyleSpans.containsKey(EasySpans.Config.CLICKABLE_LINK_TAG) && color == EasySpans.Config.ID_NULL) {
                _characterStyleSpans.remove(EasySpans.Config.COLOR_TAG)
            }
            // Always remove underline if ClickableLinkSpan is applied to avoid duplication
            if (_characterStyleSpans.containsKey(EasySpans.Config.CLICKABLE_LINK_TAG)) {
                _characterStyleSpans.remove(EasySpans.Config.UNDERLINED_TAG)
            }

            // add all custom character styles to the map if any
            customCharacterStyles?.forEach { value ->
                 val key = UUID.randomUUID().toString()
                _characterStyleSpans[key] = value
            }

            // add all custom paragraph styles to the map if any
            customParagraphStyles?.forEach { value ->
                val key = UUID.randomUUID().toString()
                _paragraphStyleSpans[key] = value
            }
        }
    }

    /**
     * Clears the character and paragraph style spans.
     */
    fun cleanUp() {
        _characterStyleSpans.clear()
        _paragraphStyleSpans.clear()
    }
}
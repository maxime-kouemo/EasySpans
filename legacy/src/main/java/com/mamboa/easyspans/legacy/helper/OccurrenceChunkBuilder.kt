package com.mamboa.easyspans.legacy.helper

import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.FontRes
import androidx.annotation.StyleRes
import com.mamboa.easyspans.legacy.EasySpans
import com.mamboa.easyspans.legacy.customspans.ClickableLinkSpan
import com.mamboa.easyspans.legacy.customspans.TextCaseSpan

/**
 * Collects per-chunk style configuration for use with OccurrenceChunk.
 * Usage: call setters to supply whichever parameters are needed.
 * Intended for one-time use/configuration before passing to OccurrenceChunk.
 */
class OccurrenceChunkBuilder {
    /** Color to apply to this chunk */
    @ColorRes
    var color: Int = EasySpans.Config.ID_NULL
        private set

    /** Background color for the chunk */
    @ColorRes
    var chunkBackgroundColor: Int = EasySpans.Config.ID_NULL
        private set

    /** Text size, as a dimension resource */
    @DimenRes
    var textSize: Int = EasySpans.Config.ID_NULL
        private set

    @StyleRes
    var style: Int = EasySpans.Config.ID_NULL
        private set

    @FontRes
    var font: Int = EasySpans.Config.ID_NULL
        private set

    /** Typeface style (normal, bold, italic, etc.) */
    var textStyle: Int = EasySpans.Config.ID_UNSET
        private set

    /** Whether to apply underline to the chunk */
    var isUnderlined: Boolean = false
        private set

    /** Whether to apply strike-through to the chunk */
    var isStrikeThrough: Boolean = false
        private set

    /** Sub/Super/NONE script styling */
    var scriptType: ScriptType = ScriptType.NONE
        private set

    /** Paragraph background color/paddings, if any */
    var paragraphBackgroundColor: SequenceBackgroundColor? = null
        private set

    /** Text casing style (upper, lower, normal) */
    var textCaseType: TextCaseSpan.TextCaseType = TextCaseSpan.TextCaseType.NORMAL
        private set

    /** Click listener for clickable links */
    var onLinkClickListener: ClickableLinkSpan.OnLinkClickListener? = null
        private set

    /** Target TextView for clickable links or paragraph background */
    var targetTextView: TextView? = null
        private set

    /**
     * Sets the color to apply to this chunk.
     * @param value the color resource ID
     * @return this OccurrenceChunkBuilder instance
     */
    fun setColor(@ColorRes value: Int) = apply {
        if (value != EasySpans.Config.ID_NULL) color = value
    }

    /**
     * Sets the background color for the chunk.
     * @param value the color resource ID
     * @return this OccurrenceChunkBuilder instance
     */
    fun setChunkBackgroundColor(@ColorRes value: Int) = apply {
        if (value != EasySpans.Config.ID_NULL) chunkBackgroundColor = value
    }

    /**
     * Sets the text style (normal, bold, italic, etc.).
     * @param value the text style
     * @return this OccurrenceChunkBuilder instance
     */
    fun setTextStyle(value: Int) = apply { textStyle = value }

    /**
     * Sets the style resource ID.
     * @param value the style resource ID
     * @return this OccurrenceChunkBuilder instance
     */
    fun setStyle(value: Int) = apply { style = value }

    /**
     * Sets the text size, as a dimension resource.
     * @param value the dimension resource ID
     * @return this OccurrenceChunkBuilder instance
     */
    fun setTextSize(@DimenRes value: Int) = apply { textSize = value }

    /**
     * Sets the font resource ID.
     * @param value the font resource ID
     * @return this OccurrenceChunkBuilder instance
     */
    fun setFont(@FontRes value: Int) = apply { font = value }

    /**
     * Sets the paragraph background color/paddings, if any.
     * @param value the paragraph background color
     * @return this OccurrenceChunkBuilder instance
     */
    fun setParagraphBackgroundColor(value: SequenceBackgroundColor) =
        apply { paragraphBackgroundColor = value }

    /**
     * Sets whether to apply underline to the chunk.
     * @return this OccurrenceChunkBuilder instance
     */
    fun isUnderlined() = apply { isUnderlined = true }

    /**
     * Sets whether to apply strike-through to the chunk.
     * @return this OccurrenceChunkBuilder instance
     */
    fun isStrikeThrough() = apply { isStrikeThrough = true }

    /**
     * Sets the script type (sub, super, none).
     * @param value the script type
     * @return this OccurrenceChunkBuilder instance
     */
    fun setScriptType(value: ScriptType) = apply { scriptType = value }

    /**
     * Sets the text casing style (upper, lower, normal).
     * @param value the text casing style
     * @return this OccurrenceChunkBuilder instance
     */
    fun setTextCaseType(value: TextCaseSpan.TextCaseType) = apply { textCaseType = value }

    /**
     * Sets the click listener for clickable links.
     * @param listener the click listener
     * @return this OccurrenceChunkBuilder instance
     */
    fun setOnLinkClickListener(listener: ClickableLinkSpan.OnLinkClickListener?) = apply {
        onLinkClickListener = listener
    }

    /**
     * Sets the target TextView for clickable links or paragraph background.
     * @param textView the target TextView
     * @return this OccurrenceChunkBuilder instance
     */
    fun setTargetTextView(textView: TextView?) = apply {
        targetTextView = textView
    }
}
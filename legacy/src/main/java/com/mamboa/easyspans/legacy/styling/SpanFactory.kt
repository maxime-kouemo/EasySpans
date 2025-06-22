package com.mamboa.easyspans.legacy.styling

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.ParagraphStyle
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.TextAppearanceSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.FontRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.mamboa.easyspans.legacy.EasySpans
import com.mamboa.easyspans.legacy.R
import com.mamboa.easyspans.legacy.customspans.ClickableLinkSpan
import com.mamboa.easyspans.legacy.customspans.PaddingBackgroundColorSpan
import com.mamboa.easyspans.legacy.customspans.TextCaseSpan
import com.mamboa.easyspans.legacy.helper.ScriptType
import com.mamboa.easyspans.legacy.helper.SequenceBackgroundColor

/**
 * Factory responsible for creating text style spans.
 * Centralizes span creation logic and improves testability.
 */
class SpanFactory(private val context: Context) {
    private val authorizedTypefaces =
        arrayListOf(Typeface.NORMAL, Typeface.BOLD, Typeface.BOLD_ITALIC, Typeface.ITALIC)
    private val authorizedTextGravities =
        arrayListOf(Gravity.LEFT, Gravity.START, Gravity.CENTER, Gravity.RIGHT, Gravity.END)

    val authorizedTextCases = arrayListOf(
        TextCaseSpan.TextCaseType.NORMAL,
        TextCaseSpan.TextCaseType.UPPER_CASE,
        TextCaseSpan.TextCaseType.LOWER_CASE,
        TextCaseSpan.TextCaseType.CAPITALIZE,
        TextCaseSpan.TextCaseType.DECAPITALIZE
    )

    /**
     * Method to define the text style's characterStyle
     * @param style - can be  NORMAL, BOLD, ITALIC, BOLD_ITALIC
     *
     * @return CharacterStyle - containing changes to apply
     */
    fun createStyleSpan(style: Int): CharacterStyle {
        val safeTypeFace = if (authorizedTypefaces.contains(style)) style else Typeface.NORMAL
        return StyleSpan(safeTypeFace)
    }

    /**
     * Method to define the general style's characterStyle
     * @param styleResId - the @StyleRes id representing the style to apply
     *
     * @return CharacterStyle - containing changes to apply
     */
    fun createTextAppearanceSpan(@StyleRes styleResId: Int): CharacterStyle {
        return TextAppearanceSpan(context, styleResId)
    }

    /**
     * Method to define the color's characterStyle
     * @param colorResId - the @colorRes id representing the color
     *
     * @return CharacterStyle - containing changes to apply
     */
    fun createForegroundColorSpan(@ColorRes colorResId: Int): CharacterStyle {
        return ForegroundColorSpan(ContextCompat.getColor(context, colorResId))
    }

    /**
     * Method to define the color's characterStyle
     * @param colorResId - the @colorRes id representing the color
     *
     * @return CharacterStyle - containing changes to apply
     */
    fun createBackgroundColorSpan(@ColorRes colorResId: Int): CharacterStyle {
        return BackgroundColorSpan(ContextCompat.getColor(context, colorResId))
    }

    /**
     * Method to define the font of the text, returning a  characterStyle
     * @param fontResId - the @FontRes id representing the font to apply
     * @param textStyle - the default textStyle (NORMAL, BOLD etc.)
     *
     * @return CharacterStyle - containing changes to apply
     */
    fun createFontSpan(@FontRes fontResId: Int, textStyle: Int): CharacterStyle {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val typeface = Typeface.create(ResourcesCompat.getFont(context, fontResId), textStyle)
            TypefaceSpan(typeface)
        } else {
            val typeface = ResourcesCompat.getFont(context, fontResId) ?: Typeface.DEFAULT
            StyleSpan(typeface.style) // Fallback for older versions
        }
    }

    /**
     * Method to define the text case of a given text, returning a characterStyle
     *
     * @return CharacterStyle - containing changes to apply
     */
    fun createTextCaseSpan(textCaseType: TextCaseSpan.TextCaseType): CharacterStyle {
        return TextCaseSpan(
            if (textCaseType in authorizedTextCases) {
                textCaseType
            } else {
                TextCaseSpan.TextCaseType.NORMAL
            }
        )
    }

    /**
     * Creates an underline span.
     */
    fun createUnderlineSpan(): CharacterStyle {
        return UnderlineSpan()
    }

    /**
     * Method to define adding a strike through line on a char, returning a characterStyle
     *
     * @return CharacterStyle - containing changes to apply
     */
    fun createStrikeThroughSpan(): CharacterStyle {
        return StrikethroughSpan()
    }

    /**
     * Method to define the text size's characterStyle
     * @param textSizeResId - the @DimenRes id representing the textSize
     *
     * @return CharacterStyle - containing changes to apply
     */
    fun createTextSizeSpan(@DimenRes textSizeResId: Int): CharacterStyle {
        val size = context.resources.getDimensionPixelSize(textSizeResId)
        return AbsoluteSizeSpan(size)
    }

    /**
     * Creates a script-type span (superscript or subscript).
     */
    fun createScriptSpan(scriptType: ScriptType): CharacterStyle? {
        return when (scriptType) {
            ScriptType.SUPER -> SuperscriptSpan()
            ScriptType.SUB -> SubscriptSpan()
            else -> null
        }
    }

    /**
     * Method to define background color to a charSequence (mainly a paragraph), returning a ParagraphStyle
     * @param sequenceBackgroundColor - attribute containing the attributes to define the background.
     *                                  see {@link SequenceBackgroundColor}
     * @param targetTextView - the textView that will receive the background color. It should be the same the receives the overall text
     *
     * @return ParagraphStyle - containing changes to apply
     */
    fun createBackgroundParagraphStyle(
        sequenceBackgroundColor: SequenceBackgroundColor,
        targetTextView: TextView?
    ): ParagraphStyle {
        if (targetTextView == null) {
            throw NullPointerException("targetTextView must be provided when paragraphBackgroundColor is set")
        }
        val colorValue = ContextCompat.getColor(context, sequenceBackgroundColor.backgroundColor)
        @DimenRes val paddingID =
            if (sequenceBackgroundColor.padding != ID_NULL) sequenceBackgroundColor.padding else R.dimen.test_no_dp
        val padding = context.resources.getDimensionPixelSize(paddingID)
        val gravity =
            if (authorizedTextGravities.contains(sequenceBackgroundColor.gravity)) sequenceBackgroundColor.gravity else Gravity.CENTER

        // This is necessary to allow a perfect display of the color with padding
        if (padding != 0) {
            targetTextView.setShadowLayer(
                padding.toFloat(),
                0.0f,
                0.0f,
                Color.TRANSPARENT
            )
            targetTextView.setPadding(
                padding,
                padding,
                padding,
                padding
            )
        }

        return PaddingBackgroundColorSpan(
            colorValue,
            padding,
            gravity
        )
    }

    /**
     * Method to make a given text clickable, returning a characterStyle
     * @param targetTextView - the TextView that will receive the clickable span
     * @param colorResId - the @ColorRes id representing the color of the link
     * @param isUnderlined - whether the link should be underlined
     * @param onClickListener - the listener to handle link clicks
     * @return CharacterStyle - containing changes to apply
     */
    fun createClickableLinkSpan(
        targetTextView: TextView,
        @ColorRes colorResId: Int,
        isUnderlined: Boolean,
        onClickListener: ClickableLinkSpan.OnLinkClickListener
    ): CharacterStyle {
        targetTextView.movementMethod = LinkMovementMethod.getInstance()
        val realColor = if (colorResId == ID_NULL) {
            targetTextView.currentTextColor
        } else {
            ContextCompat.getColor(context, colorResId)
        }
        return ClickableLinkSpan(realColor, isUnderlined, onClickListener)
    }

    /**
     * Method to set a text as superScript, returning a characterStyle
     *
     * @return CharacterStyle - containing changes to apply
     */
    fun createSuperScriptCharStyle(): CharacterStyle {
        return SuperscriptSpan()
    }

    /**
     * Method to set a text as subScript, returning a characterStyle
     *
     * @return CharacterStyle - containing changes to apply
     */
    fun createSubScriptCharStyle(): CharacterStyle {
        return SubscriptSpan()
    }

    companion object {
        private const val ID_NULL = 0

        private fun SpannableStringBuilder.applyTextCaseOnTextSection(
            start: Int,
            end: Int,
            textCaseType: TextCaseSpan.TextCaseType = TextCaseSpan.TextCaseType.NORMAL
        ) {
            // Transform the text section based on textCaseType
            val chunk = when (textCaseType) {
                TextCaseSpan.TextCaseType.UPPER_CASE -> this.substring(start, end).uppercase()
                TextCaseSpan.TextCaseType.LOWER_CASE -> this.substring(start, end).lowercase()
                TextCaseSpan.TextCaseType.CAPITALIZE -> this.substring(start, end).replaceFirstChar { it.titlecase() }
                TextCaseSpan.TextCaseType.DECAPITALIZE -> this.substring(start, end).replaceFirstChar { it.lowercase() }
                else -> this.substring(start, end) // NORMAL or any other case
            }
            this.replace(start, end, chunk)
        }

        fun SpannableStringBuilder.setCustomSpan(
            tag: String,
            span: CharacterStyle,
            startIndex: Int,
            endIndex: Int,
        )  {
            if (tag == EasySpans.Config.TEXT_CASE_TYPE_TAG || span is TextCaseSpan) {
                this.applyTextCaseOnTextSection(
                    startIndex,
                    endIndex,
                    (span as TextCaseSpan).textCaseType
                )
            } else {
                this.setSpan(span, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }
}
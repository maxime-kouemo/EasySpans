package com.mamboa.easyspans.legacy.customspans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan
import java.util.Locale
import kotlin.math.roundToInt

/**
 * IMPORTANT: TextCaseSpan Handling & Visual Styling
 *
 * Issue:
 * TextCaseSpan extends ReplacementSpan, which replaces the entire text drawing process.
 * When applied, it overrides all other visual spans (color, style, etc.) on the same text chunk,
 * reverting to default text appearance and only applying the case transformation.
 *
 * Solution:
 * 1. Detect TextCaseSpan in the builder configuration
 * 2. Instead of applying TextCaseSpan directly [{@link com.mamboa.easyspans.customspans.TextCaseSpan#setCustomSpan}]:
 *    - Extract the desired case transformation from TextCaseSpan's tag
 *    - Apply the case transformation directly to the text chunk
 *    - In the end, no new span is created
 * 3. Apply remaining visual spans normally
 *
 * This approach preserves all visual styling while maintaining the desired case transformation.
 */


/**
 * This class implements a custom span, allowing us to edit the text case of a charSequence.
 * The text case types supported are :
 * - upper case: all the text is set to upper case
 * - lower case: all the text is set to lower case
 * - normal: will let the text unchanged
 * - capitalize: will set the first letter of the text to upper case
 * - decapitalize: will set the first letter of the text to lower case
 */
class TextCaseSpan(val textCaseType: TextCaseType) : ReplacementSpan() {

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int =
        paint.measureText(text, start, end).roundToInt()

    override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        canvas.drawText(
            when (textCaseType) {
                TextCaseType.NORMAL -> text.toString()
                TextCaseType.UPPER_CASE -> text.toString().uppercase()
                TextCaseType.LOWER_CASE -> text.toString().lowercase()
                TextCaseType.CAPITALIZE -> text.toString()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                TextCaseType.DECAPITALIZE -> text.toString().replaceFirstChar { it.lowercase(Locale.getDefault()) }

            },
            start, end, x, y.toFloat(), paint)
    }

    enum class TextCaseType {
        NORMAL,
        UPPER_CASE,
        LOWER_CASE,
        CAPITALIZE,
        DECAPITALIZE
    }
}

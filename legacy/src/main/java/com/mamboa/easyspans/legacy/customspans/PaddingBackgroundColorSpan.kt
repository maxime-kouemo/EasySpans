package com.mamboa.easyspans.legacy.customspans

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.style.LineBackgroundSpan
import android.view.Gravity
import android.view.View
import androidx.core.text.layoutDirection
import java.util.Locale

/*
 * When trying to put a background color on the text only within textview, there is no padding. This
 * class allows a padding in this circumstance. The source is linked below.
 * Was edited by mamboa, to add a gravity switcher because the default code in the source is only working for gravity start
 * Source: https://medium.com/@tokudu/android-adding-padding-to-backgroundcolorspan-179ab4fae187
 */
/**
 * A custom LineBackgroundSpan that allows adding a background color with padding around the text.
 * This span can be used to create a visually appealing background for text in a TextView,
 * with the ability to specify padding and gravity.
 * @param backgroundColor The color of the background.
 * @param padding The padding to be applied around the text.
 * @param gravity The gravity of the text, which determines how the background is aligned relative to the text.
 */
class PaddingBackgroundColorSpan(
    internal val backgroundColor: Int,
    internal val padding: Int,
    internal val gravity: Int
) :
    LineBackgroundSpan {
    private val backgroundRect = Rect()

    override fun drawBackground(
        canvas: Canvas,
        paint: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lineNumber: Int
    ) {
        val textWidth = Math.round(paint.measureText(text, start, end))
        val paintColor = paint.color

        val finalTop = top - if (lineNumber == 0) padding / 2 else -(padding / 2)
        val finalBottom = bottom + padding / 2
        val isLeftToRight = Locale.getDefault().layoutDirection == View.LAYOUT_DIRECTION_LTR
        // Draw the background
        backgroundRect.set(
            when {
                gravity == Gravity.LEFT || (isLeftToRight && gravity == Gravity.START) || (!isLeftToRight && gravity == Gravity.END) -> getLeftRect(
                    left,
                    finalTop,
                    right,
                    finalBottom,
                    textWidth
                )

                gravity == Gravity.CENTER -> getCenterRect(
                    left,
                    finalTop,
                    right,
                    finalBottom,
                    textWidth
                )

                gravity == Gravity.RIGHT || (isLeftToRight && gravity == Gravity.END) || (!isLeftToRight && gravity == Gravity.START) -> getRightRect(
                    left,
                    finalTop,
                    right,
                    finalBottom,
                    textWidth
                )

                else -> {
                    getLeftRect(left, finalTop, right, finalBottom, textWidth)
                }
            }
        )

        paint.color = backgroundColor
        canvas.drawRect(backgroundRect, paint)
        paint.color = paintColor
    }

    /**
     * Method to process the rectangle where the background (with its padding) will be drawn when the
     * text gravity left
     * @param left - left coordinate of the textView relative to its parent
     * @param top - top coordinate of the textView relative to its parent
     * @param right - right coordinate of the textView relative to its parent
     * @param bottom - bottom coordinate of the textView relative to its parent
     * @param textWidth - the width of the textView
     *
     * @return Rect - containing the coordinates to draw the background
     */
    private fun getLeftRect(left: Int, top: Int, right: Int, bottom: Int, textWidth: Int): Rect {
        return Rect(left - padding, top, left + textWidth + padding, bottom)
    }

    /**
     * Method to process the rectangle where the background (with its padding) will be drawn when the
     * text gravity right
     * @param left - left coordinate of the textView relative to its parent
     * @param top - top coordinate of the textView relative to its parent
     * @param right - right coordinate of the textView relative to its parent
     * @param bottom - bottom coordinate of the textView relative to its parent
     * @param textWidth - the width of the textView
     *
     * @return Rect - containing the coordinates to draw the background
     */
    private fun getRightRect(left: Int, top: Int, right: Int, bottom: Int, textWidth: Int): Rect {
        return Rect(right - textWidth - padding, top, right + padding, bottom)
    }

    /**
     * Method to process the rectangle where the background (with its padding) will be drawn when the
     * text gravity is center
     * @param left - left coordinate of the textView relative to its parent
     * @param top - top coordinate of the textView relative to its parent
     * @param right - right coordinate of the textView relative to its parent
     * @param bottom - bottom coordinate of the textView relative to its parent
     * @param textWidth - the width of the textView
     *
     * @return Rect - containing the coordinates to draw the background
     */
    private fun getCenterRect(left: Int, top: Int, right: Int, bottom: Int, textWidth: Int): Rect {
        val diff = kotlin.math.abs((right - left) / 2 - textWidth / 2)
        return Rect(left + diff - padding, top, right - diff + padding, bottom)
    }
}
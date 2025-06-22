package com.mamboa.easyspans.legacy.customspans

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextUtils
import android.text.style.LineBackgroundSpan
import android.view.Gravity
import android.view.View
import java.util.*
import androidx.core.text.layoutDirection

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
 * @param mBackgroundColor The color of the background.
 * @param mPadding The padding to be applied around the text.
 * @param gravity The gravity of the text, which determines how the background is aligned relative to the text.
 */
class PaddingBackgroundColorSpan(private val mBackgroundColor: Int, private val mPadding: Int, private val gravity: Int) :
    LineBackgroundSpan {
    private val backgroundRect = Rect()

    override fun drawBackground(c: Canvas, p: Paint, left: Int, right: Int, top: Int, baseline: Int, bottom: Int, text: CharSequence, start: Int, end: Int, lnum: Int) {
        val textWidth = Math.round(p.measureText(text, start, end))
        val paintColor = p.color

        val finalTop = top - if (lnum == 0) mPadding / 2 else -(mPadding / 2)
        val finalBottom = bottom + mPadding / 2
        val isLeftToRight = Locale.getDefault().layoutDirection == View.LAYOUT_DIRECTION_LTR
        // Draw the background
        backgroundRect.set(when {
            gravity == Gravity.LEFT || (isLeftToRight &&  gravity == Gravity.START) || (!isLeftToRight &&  gravity == Gravity.END) -> getLeftRect(left, finalTop, right, finalBottom, textWidth)
            gravity == Gravity.CENTER -> getCenterRect(left, finalTop, right, finalBottom, textWidth)
            gravity == Gravity.RIGHT || (isLeftToRight &&  gravity == Gravity.END) || (!isLeftToRight &&  gravity == Gravity.START) -> getRightRect(left, finalTop, right, finalBottom, textWidth)
            else -> {
                getLeftRect(left, finalTop, right, finalBottom, textWidth)
            }
        })

        p.color = mBackgroundColor
        c.drawRect(backgroundRect, p)
        p.color = paintColor
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
        return Rect(left - mPadding, top, left + textWidth + mPadding, bottom)
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
        return Rect(right - textWidth - mPadding, top, right + mPadding, bottom)
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
        return Rect(left + diff - mPadding, top, right - diff + mPadding, bottom)
    }
}
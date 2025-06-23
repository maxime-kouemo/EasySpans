package com.mamboa.easyspans.legacy.customspans

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.annotation.ColorInt

/**
 * A custom ClickableSpan that allows for clickable links with customizable color and underline options.
 * This span can be used to create clickable links in a TextView, with the ability to specify
 * the link color and whether the link should be underlined.
 *
 * @param linkColor The color of the link text.
 * @param isLinkUnderLine Whether the link text should be underlined.
 * @param onLinkClickedListener Optional listener for link click events.
 */
class ClickableLinkSpan(
    @ColorInt internal val linkColor: Int,
    internal val isLinkUnderLine: Boolean = true,
    internal val onLinkClickedListener: OnLinkClickListener?
) : ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = isLinkUnderLine
        ds.color = linkColor
    }

    override fun onClick(view: View) {
        onLinkClickedListener?.onLinkClick(view)
    }

    interface OnLinkClickListener {
        fun onLinkClick(view: View)
    }
}
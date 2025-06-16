package com.mamboa.easyspans.legacy.customspans

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.annotation.ColorInt

class ClickableLinkSpan (@ColorInt private val linkColor: Int,
                         private val isLinkUnderLine: Boolean = true,
                         private val onLinkClickedListener: OnLinkClickListener?) : ClickableSpan() {

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
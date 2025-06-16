package com.mamboa.easyspans.legacy.helper

import android.view.Gravity
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import com.mamboa.easyspans.legacy.EasySpans

/**
 * Holds background styling information for a chunk within a CharSequence.
 *
 * @property backgroundColor The background color resource to apply.
 * @property padding The padding (dimension resource) around the text background.
 * @property gravity The horizontal alignment (default: center).
 */
data class SequenceBackgroundColor(
    @ColorRes val backgroundColor: Int = EasySpans.Config.ID_NULL,
    @DimenRes val padding: Int = EasySpans.Config.ID_NULL,
    val gravity: Int = Gravity.CENTER
)

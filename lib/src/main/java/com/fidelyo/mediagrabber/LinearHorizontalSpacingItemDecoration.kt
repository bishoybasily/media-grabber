package com.fidelyo.mediagrabber

import android.graphics.Rect

/**
 * Created by bishoy on 6/17/17.
 */
class LinearHorizontalSpacingItemDecoration(spacing: Int) : SpacingItemDecoration(spacing) {

    override fun applySpacing(position: Int, outRect: Rect) {
        if (position < 1) {
            outRect.left = spacing
        }
        outRect.right = spacing
        outRect.top = spacing
        outRect.bottom = spacing
    }
}
package com.fidelyo.imagegrabber

import android.graphics.Rect
import com.fidelyo.imagegrabber.SpacingItemDecoration

/**
 * Created by bishoy on 6/17/17.
 */
class LinearVerticalSpacingItemDecoration(spacing: Int) : SpacingItemDecoration(spacing) {

    override fun applySpacing(position: Int, outRect: Rect) {
        outRect.left = spacing
        outRect.right = spacing
        if (position < 1) {
            outRect.top = spacing
        }
        outRect.bottom = spacing
    }

}
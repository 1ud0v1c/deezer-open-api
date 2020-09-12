package com.ludovic.vimont.deezeropenapi.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Add equal space between each item of a GridLayoutManager. Useful for the HomeAlbumAdapter.
 */
class GridSpacingItemDecoration(private val spanCount: Int,
                                private val spacing: Int,
                                private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State) {
        val position: Int = parent.getChildAdapterPosition(view)
        val column: Int = position % spanCount

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount
            if (position < spanCount) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.top = spacing
            }
        }
    }
}
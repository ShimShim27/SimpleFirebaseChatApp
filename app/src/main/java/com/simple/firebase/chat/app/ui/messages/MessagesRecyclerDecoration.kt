package com.simple.firebase.chat.app.ui.messages

import android.graphics.Rect
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

class MessagesRecyclerDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val pos = parent.children.indexOf(view)
        outRect.top = if (pos == 0) 20 else 0
        outRect.bottom = if (pos >= parent.childCount - 1) 20 else 0
        outRect.left = 30
        outRect.right = 30

    }
}
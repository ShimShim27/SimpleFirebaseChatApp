package com.simple.firebase.chat.app.ui.main

import android.graphics.Rect
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

class ConversationRecyclerDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val pos = parent.children.indexOf(view)
        outRect.top = if (pos == 0) 10 else 0
        outRect.bottom = if (parent.childCount <= pos) 30 else 0
    }
}
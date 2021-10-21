package com.simple.firebase.chat.app.custom

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment

open class BaseDialogFragment(res: Int, private val width: Int, private val height: Int) :
    DialogFragment(res) {


    override fun onResume() {
        super.onResume()

        val window = dialog?.window
        if (window != null) {
            val layoutParams = window.attributes as ViewGroup.LayoutParams
            layoutParams.width = width
            layoutParams.height = height
            window.attributes = layoutParams as WindowManager.LayoutParams

            val back = ColorDrawable(Color.TRANSPARENT)
            val inset = InsetDrawable(back, 20)
            window.setBackgroundDrawable(inset)
        }
    }
}
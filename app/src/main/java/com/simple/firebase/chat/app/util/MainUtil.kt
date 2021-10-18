package com.simple.firebase.chat.app.util

import android.content.Context
import com.simple.firebase.chat.app.dagger.DaggerViewModelComponent
import com.simple.firebase.chat.app.dagger.ViewModelComponent
import com.simple.firebase.chat.app.dagger.ViewModelModule

object MainUtil {
    fun getViewModuleComponent(context: Context): ViewModelComponent =
        DaggerViewModelComponent.builder().viewModelModule(ViewModelModule(context)).build()
}
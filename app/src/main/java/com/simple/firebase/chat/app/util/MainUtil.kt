package com.simple.firebase.chat.app.util

import android.content.Context
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.simple.firebase.chat.app.dagger.DaggerViewModelComponent
import com.simple.firebase.chat.app.dagger.ViewModelComponent
import com.simple.firebase.chat.app.dagger.ViewModelModule

object MainUtil {
    fun getViewModuleComponent(context: Context): ViewModelComponent =
        DaggerViewModelComponent.builder().viewModelModule(ViewModelModule(context)).build()

    fun makeToast(context: Context, message: Any?) {
        Toast.makeText(context, "$message", Toast.LENGTH_SHORT).show()
    }

    fun buildGoogleSignInClient(context: Context, webClientId: String): GoogleSignInClient {
        val options = GoogleSignInOptions.Builder()
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, options)
    }
}
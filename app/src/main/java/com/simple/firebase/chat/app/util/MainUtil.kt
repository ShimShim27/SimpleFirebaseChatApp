package com.simple.firebase.chat.app.util

import android.content.Context
import android.net.Uri
import android.util.TypedValue
import android.widget.ImageView
import android.widget.Toast
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.size.Scale
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.simple.firebase.chat.app.dagger.DaggerViewModelComponent
import com.simple.firebase.chat.app.dagger.ViewModelComponent
import com.simple.firebase.chat.app.dagger.ViewModelModule
import java.util.*

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


    fun loadImageFromUrl(context: Context, into: ImageView, url: Uri) {
        val imageLoader = ImageLoader.Builder(context)
            .componentRegistry { add(SvgDecoder(context)) }
            .build()

        val request = ImageRequest.Builder(context)
            .crossfade(true)
            .crossfade(100)
            .data(url)
            .scale(Scale.FIT)
            .target(into)
            .build()

        imageLoader.enqueue(request)


    }

    fun getColorFromAttr(context: Context, attr: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    fun getRandomAvatarLink():String = "https://avatars.dicebear.com/api/bottts/${Random().nextLong()}.svg"
}
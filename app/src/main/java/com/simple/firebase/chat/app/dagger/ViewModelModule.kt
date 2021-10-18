package com.simple.firebase.chat.app.dagger

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.simple.firebase.chat.app.repo.FirestoreRepo
import com.simple.firebase.chat.app.ui.main.MainActivityViewModel
import com.simple.firebase.chat.app.ui.messages.MessagesActivityViewModel
import dagger.Module
import dagger.Provides


@Module
class ViewModelModule(private val context: Context) {


    @Provides
    fun providesMainActivityViewModel(firestoreRepo: FirestoreRepo): MainActivityViewModel {
        return ViewModelProvider(context as ViewModelStoreOwner,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return MainActivityViewModel(firestoreRepo) as T
                }

            }).get(MainActivityViewModel::class.java)
    }


    @Provides
    fun providesMessagesActivityViewModel(firestoreRepo: FirestoreRepo): MessagesActivityViewModel {
        return ViewModelProvider(context as ViewModelStoreOwner,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return MessagesActivityViewModel(firestoreRepo) as T
                }

            }).get(MessagesActivityViewModel::class.java)
    }
}
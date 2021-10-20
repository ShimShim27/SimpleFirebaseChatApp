package com.simple.firebase.chat.app.dagger

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.paging.*
import com.simple.firebase.chat.app.datasource.pagingsource.ConversationsPagingDataSource
import com.simple.firebase.chat.app.datasource.repo.FirestoreRepo
import com.simple.firebase.chat.app.model.Conversation
import com.simple.firebase.chat.app.ui.main.MainActivityViewModel
import com.simple.firebase.chat.app.ui.messages.MessagesActivityViewModel
import com.simple.firebase.chat.app.ui.searchuser.SearchUserFragmentViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class ViewModelModule(private val context: Context) {

    @Provides
    fun providesMainActivityViewModel(
        firestoreRepo: FirestoreRepo
    ): MainActivityViewModel {
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


    @Provides
    fun providesSearchUserFragmentViewModel(firestoreRepo: FirestoreRepo): SearchUserFragmentViewModel {
        return ViewModelProvider(context as ViewModelStoreOwner,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return SearchUserFragmentViewModel(firestoreRepo) as T
                }

            }).get(SearchUserFragmentViewModel::class.java)
    }


}
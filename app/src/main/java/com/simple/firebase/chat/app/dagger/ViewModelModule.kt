package com.simple.firebase.chat.app.dagger

import android.content.Context
import androidx.lifecycle.*
import com.simple.firebase.chat.app.datasource.repo.FirebaseRepo
import com.simple.firebase.chat.app.ui.account.AccountFragmentViewModel
import com.simple.firebase.chat.app.ui.login.LoginActivityViewModel
import com.simple.firebase.chat.app.ui.main.MainActivityViewModel
import com.simple.firebase.chat.app.ui.messages.MessagesActivityViewModel
import com.simple.firebase.chat.app.ui.searchuser.SearchUserFragmentViewModel
import dagger.Module
import dagger.Provides


@Module
class ViewModelModule(private val context: Context) {

    @Provides
    fun providesMainActivityViewModel(
        firebaseRepo: FirebaseRepo
    ): MainActivityViewModel {
        return ViewModelProvider(context as ViewModelStoreOwner,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return MainActivityViewModel(firebaseRepo) as T
                }

            }).get(MainActivityViewModel::class.java)
    }


    @Provides
    fun providesMessagesActivityViewModel(firebaseRepo: FirebaseRepo): MessagesActivityViewModel {
        return ViewModelProvider(context as ViewModelStoreOwner,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return MessagesActivityViewModel(firebaseRepo) as T
                }

            }).get(MessagesActivityViewModel::class.java)
    }


    @Provides
    fun providesSearchUserFragmentViewModel(firebaseRepo: FirebaseRepo): SearchUserFragmentViewModel {
        return ViewModelProvider(context as ViewModelStoreOwner,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return SearchUserFragmentViewModel(firebaseRepo) as T
                }

            }).get(SearchUserFragmentViewModel::class.java)
    }

    @Provides
    fun providesLoginActivityViewModel(firebaseRepo: FirebaseRepo): LoginActivityViewModel {
        return ViewModelProvider(context as ViewModelStoreOwner,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return LoginActivityViewModel(firebaseRepo) as T
                }

            }).get(LoginActivityViewModel::class.java)
    }


    @Provides
    fun providesAccountFragmentViewModel(firebaseRepo: FirebaseRepo): AccountFragmentViewModel {
        return ViewModelProvider(context as ViewModelStoreOwner,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return AccountFragmentViewModel(firebaseRepo) as T
                }

            }).get(AccountFragmentViewModel::class.java)
    }


}
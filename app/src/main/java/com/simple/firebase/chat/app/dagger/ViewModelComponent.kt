package com.simple.firebase.chat.app.dagger

import com.simple.firebase.chat.app.ui.account.AccountFragmentViewModel
import com.simple.firebase.chat.app.ui.login.LoginActivityViewModel
import com.simple.firebase.chat.app.ui.main.MainActivityViewModel
import com.simple.firebase.chat.app.ui.messages.MessagesActivityViewModel
import com.simple.firebase.chat.app.ui.searchuser.SearchUserFragmentViewModel
import dagger.Component

@Component(modules = [ViewModelModule::class])
interface ViewModelComponent {
    fun createMainActivityViewModel(): MainActivityViewModel
    fun createMessagesActivityViewModel(): MessagesActivityViewModel
    fun createSearchFragmentUserViewModel(): SearchUserFragmentViewModel
    fun createLoginActivityViewModel():LoginActivityViewModel
    fun createAccountFragmentViewModel():AccountFragmentViewModel
}
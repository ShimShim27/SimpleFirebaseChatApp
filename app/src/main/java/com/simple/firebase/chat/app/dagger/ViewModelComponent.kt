package com.simple.firebase.chat.app.dagger

import com.simple.firebase.chat.app.model.Message
import com.simple.firebase.chat.app.ui.main.MainActivityViewModel
import com.simple.firebase.chat.app.ui.messages.MessagesActivityViewModel
import dagger.Component

@Component(modules = [ViewModelModule::class])
interface ViewModelComponent {
    fun createMainActivityViewModel(): MainActivityViewModel
    fun createMessagesActivityViewModel(): MessagesActivityViewModel
}
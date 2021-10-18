package com.simple.firebase.chat.app.ui.messages

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.simple.firebase.chat.app.model.Message
import com.simple.firebase.chat.app.repo.FirestoreRepo
import java.lang.Exception

class MessagesActivityViewModel(private val firestoreRepo: FirestoreRepo) : ViewModel() {
    val messagesLiveData = MutableLiveData(mutableListOf<Message>())
    private var messagesFetched = false

    fun getMessagesIfNotYet(otherUserId:String) {
        if (!messagesFetched) {
            messagesFetched = true

            val onSuccess = { messages: List<Message> ->
                messagesLiveData.value?.addAll(messages)
                messagesLiveData.postValue(messagesLiveData.value)
            }

            val onFailure = { e: Exception ->

            }
            firestoreRepo.getMessages(otherUserId, onSuccess, onFailure)
        }
    }


}
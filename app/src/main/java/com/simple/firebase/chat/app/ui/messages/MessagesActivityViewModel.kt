package com.simple.firebase.chat.app.ui.messages

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.simple.firebase.chat.app.model.Message
import com.simple.firebase.chat.app.repo.FirestoreRepo
import java.lang.Exception

class MessagesActivityViewModel(private val firestoreRepo: FirestoreRepo) : ViewModel() {
    val messagesLiveData = MutableLiveData(mutableListOf<Message>())
    private var messagesFetched = false

    fun getMessagesIfNotYet(otherUserId: String) {
        if (!messagesFetched) {
            messagesFetched = true

            val onSuccess = { messages: List<Message> ->
                messagesLiveData.postValue(messages.toMutableList())
            }

            val onFailure = { e: Exception ->

            }
            firestoreRepo.getMessages(otherUserId, onSuccess, onFailure)
        }
    }


    fun sendMessage(message: String, targetId: String) {
        val onSuccess = {

        }

        val onFailure = { e: Exception ->

        }

        firestoreRepo.sendMessage(targetId, message, onSuccess, onFailure)
    }

    fun getUserId(): String = firestoreRepo.userId


}
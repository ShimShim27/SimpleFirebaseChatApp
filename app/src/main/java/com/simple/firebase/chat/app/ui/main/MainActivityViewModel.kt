package com.simple.firebase.chat.app.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.simple.firebase.chat.app.model.Conversation
import com.simple.firebase.chat.app.repo.FirestoreRepo
import java.lang.Exception

class MainActivityViewModel(private val firestoreRepo: FirestoreRepo) : ViewModel() {
    val conversationsLiveData = MutableLiveData(mutableListOf<Conversation>())
    val gotoMessagesLiveData = MutableLiveData<String?>()

    private var conversationsFetched = false

    fun getConversationsIfNotYet() {
        if (!conversationsFetched) {
            conversationsFetched = true

            val onSuccess = { conversations: List<Conversation> ->
                conversationsLiveData.value?.addAll(conversations)
                conversationsLiveData.value = conversationsLiveData.value
            }

            val onFailure = { e: Exception ->

            }

            firestoreRepo.getConversations(onSuccess, onFailure)
        }
    }

    fun gotoMessages(otherUserId: String) {
        gotoMessagesLiveData.postValue(otherUserId)
    }

}
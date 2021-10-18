package com.simple.firebase.chat.app.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.simple.firebase.chat.app.model.Conversation
import com.simple.firebase.chat.app.repo.FirestoreRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivityViewModel(private val firestoreRepo: FirestoreRepo) : ViewModel() {
    val conversationsLiveData = MutableLiveData(mutableListOf<Conversation>())
    val gotoMessagesLiveData = MutableLiveData<String?>()

    private var conversationsFetched = false


    fun getConversationsIfNotYet() {
        CoroutineScope(Dispatchers.Unconfined).launch {
            if (!conversationsFetched) {
                conversationsFetched = true

                val onSuccess = { conversations: List<Conversation> ->
                    conversationsLiveData.postValue(conversations.toMutableList())
                }

                val onFailure = { e: Exception ->

                }

                firestoreRepo.getConversations(onSuccess, onFailure)
            }
        }
    }

    fun gotoMessages(otherUserId: String) {
        gotoMessagesLiveData.postValue(otherUserId)
    }

}
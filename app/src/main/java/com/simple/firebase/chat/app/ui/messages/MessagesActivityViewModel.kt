package com.simple.firebase.chat.app.ui.messages

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.google.firebase.firestore.DocumentSnapshot
import com.simple.firebase.chat.app.config.Config
import com.simple.firebase.chat.app.datasource.pagingsource.MessagesPagingDataSource
import com.simple.firebase.chat.app.model.Message
import com.simple.firebase.chat.app.datasource.repo.FirestoreRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class MessagesActivityViewModel(private val firestoreRepo: FirestoreRepo) : ViewModel() {
    private lateinit var currentMessagesSource: MessagesPagingDataSource
    lateinit var otherUserId: String

    val messagesLiveData = Pager(config = PagingConfig(Config.MESSAGES_LIST_SIZE.toInt()),
        pagingSourceFactory = {
            currentMessagesSource = MessagesPagingDataSource(firestoreRepo, otherUserId)
            currentMessagesSource
        }
    ).liveData.cachedIn(viewModelScope)

    private var messagesUpdateListenerSet = false

    fun setMessagesUpdateListener() {
        if (!messagesUpdateListenerSet) {
            val onSuccess = { _: DocumentSnapshot?, _: List<Message> ->
                currentMessagesSource.invalidate()
            }

            val onFailure = { e: Exception ->
            }
            firestoreRepo.getMessagesViaSnapshot(
                Config.MESSAGES_LIST_SIZE,
                null,
                otherUserId,
                onSuccess,
                onFailure
            )
            messagesUpdateListenerSet = true
        }
    }


    fun sendMessage(message: String) {
        val onSuccess = {

        }

        val onFailure = { e: Exception ->

        }

        CoroutineScope(Dispatchers.Unconfined).launch {
            firestoreRepo.sendMessage(otherUserId, message, onSuccess, onFailure)
        }
    }

    fun getUserId(): String = firestoreRepo.userId


}
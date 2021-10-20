package com.simple.firebase.chat.app.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.google.firebase.firestore.DocumentSnapshot
import com.simple.firebase.chat.app.datasource.pagingsource.ConversationsPagingDataSource
import com.simple.firebase.chat.app.model.Conversation
import com.simple.firebase.chat.app.datasource.repo.FirestoreRepo
import com.simple.firebase.chat.app.config.Config

class MainActivityViewModel(
    private val firestoreRepo: FirestoreRepo,
) : ViewModel() {
    private lateinit var currentConversationSource: ConversationsPagingDataSource
    private var conversationUpdatedSet = false
    val conversationsLiveData: LiveData<PagingData<Conversation>> =
        Pager(config = PagingConfig(
            Config.CONVERSATION_LIST_SIZE.toInt(),
            enablePlaceholders = false
        ),
            pagingSourceFactory = {
                currentConversationSource = ConversationsPagingDataSource(firestoreRepo)
                currentConversationSource
            }
        ).liveData.cachedIn(viewModelScope)

    val gotoMessagesLiveData = MutableLiveData<String?>()

    fun gotoMessages(otherUserId: String) {
        gotoMessagesLiveData.postValue(otherUserId)
    }

    fun setConversationUpdateListener() {
        if (!conversationUpdatedSet) {
            val onSuccess = { _: DocumentSnapshot?, _: List<Conversation> ->
                currentConversationSource.invalidate()
            }
            firestoreRepo.getConversationsViaSnapshot(
                Config.CONVERSATION_LIST_SIZE,
                null,
                onSuccess,
                null
            )
            conversationUpdatedSet = true
        }
    }



}
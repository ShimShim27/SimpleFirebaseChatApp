package com.simple.firebase.chat.app.datasource.pagingsource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.simple.firebase.chat.app.config.Config
import com.simple.firebase.chat.app.datasource.repo.FirebaseRepo
import com.simple.firebase.chat.app.model.Conversation
import kotlinx.coroutines.*
import java.lang.Exception

class ConversationsPagingDataSource(private val firebaseRepo: FirebaseRepo) :
    PagingSource<DocumentSnapshot, Conversation>() {
    private val registrations = mutableListOf<ListenerRegistration>()
    private var lastKey: DocumentSnapshot? = null
    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Conversation>): DocumentSnapshot? =
        null

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Conversation> {
        var done = false
        var lastResultSnapshot: DocumentSnapshot? = null
        val conversationsList = mutableListOf<Conversation>()

        val onSuccess = { lastSnapshot: DocumentSnapshot?, conversations: List<Conversation> ->
            lastResultSnapshot = lastSnapshot
            conversationsList.addAll(conversations)
            done = true
        }

        val onFailure = { _: Exception ->
            done = true
        }


        registrations.forEach { it.remove() }
        registrations.add(
            firebaseRepo.getConversationsViaSnapshot(
                Config.CONVERSATION_LIST_SIZE,
                params.key,
                onSuccess,
                onFailure
            )
        )

        while (!done) {
            delay(Config.PAGING_DATA_SOURCE_SNAPSHOT_DELAY_WAITING)
        }

        conversationsList.forEach { conversation ->
            firebaseRepo.getUserWithUserId(conversation.partnerUserId, {
                if (it != null) conversation.name = it.name
            }, {

            })
        }

        lastKey = params.key
        return LoadResult.Page(conversationsList, params.key, lastResultSnapshot)
    }
}
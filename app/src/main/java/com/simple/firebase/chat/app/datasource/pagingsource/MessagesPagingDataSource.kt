package com.simple.firebase.chat.app.datasource.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.simple.firebase.chat.app.config.Config
import com.simple.firebase.chat.app.datasource.repo.FirestoreRepo
import com.simple.firebase.chat.app.model.Message
import kotlinx.coroutines.delay
import java.lang.Exception

class MessagesPagingDataSource(
    private val firestoreRepo: FirestoreRepo,
    private val otherUserId: String
) : PagingSource<DocumentSnapshot, Message>() {
    private var lastKey: DocumentSnapshot? = null
    private val registrations = mutableListOf<ListenerRegistration>()
    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Message>): DocumentSnapshot? =
        lastKey

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Message> {
        var done = false
        var firstDocumentSnapshot: DocumentSnapshot? = null
        val messagesList = mutableListOf<Message>()

        val onSuccess = { firstSnapshot: DocumentSnapshot?, messages: List<Message> ->
            firstDocumentSnapshot = firstSnapshot
            messagesList.addAll(messages)
            done = true
        }

        val onFailure = { _: Exception ->
            done = true
        }

        registrations.forEach { it.remove() }
        registrations.add(
            firestoreRepo.getMessagesViaSnapshot(
                Config.MESSAGES_LIST_SIZE,
                params.key,
                otherUserId,
                onSuccess,
                onFailure
            )
        )


        while (!done) {
            delay(Config.PAGING_DATA_SOURCE_SNAPSHOT_DELAY_WAITING)
        }

        lastKey = params.key
        return LoadResult.Page(messagesList, firstDocumentSnapshot, params.key)
    }

}
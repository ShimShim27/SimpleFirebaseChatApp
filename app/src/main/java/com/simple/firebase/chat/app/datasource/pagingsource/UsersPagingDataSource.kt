package com.simple.firebase.chat.app.datasource.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.simple.firebase.chat.app.config.Config
import com.simple.firebase.chat.app.datasource.repo.FirestoreRepo
import com.simple.firebase.chat.app.model.User
import kotlinx.coroutines.delay
import java.lang.Exception

class UsersPagingDataSource(
    private val firestoreRepo: FirestoreRepo,
    private val startWithQuery: String
) :
    PagingSource<DocumentSnapshot, User>() {
    override fun getRefreshKey(state: PagingState<DocumentSnapshot, User>): DocumentSnapshot? = null

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, User> {
        val usersList = mutableListOf<User>()
        var lastDocumentSnapshot: DocumentSnapshot? = null

        if (startWithQuery.trim().isNotEmpty()) {
            var done = false
            val onSuccess = { lastSnapshot: DocumentSnapshot?, users: List<User> ->
                usersList.addAll(users)
                lastDocumentSnapshot = lastSnapshot
                done = true
            }

            val onFailure = { e: Exception ->
                done = true
            }

            firestoreRepo.getUsersViaSnapshot(
                startWithQuery,
                Config.USERS_LIST_SIZE,
                params.key,
                onSuccess,
                onFailure
            )

            while (!done) {
                delay(Config.PAGING_DATA_SOURCE_SNAPSHOT_DELAY_WAITING)
            }

        }

        return LoadResult.Page(usersList, params.key, lastDocumentSnapshot)
    }
}
package com.simple.firebase.chat.app.ui.searchuser

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.simple.firebase.chat.app.datasource.pagingsource.UsersPagingDataSource
import com.simple.firebase.chat.app.datasource.repo.FirestoreRepo

class SearchUserFragmentViewModel(private val firestoreRepo: FirestoreRepo) : ViewModel() {
    private lateinit var currentUserDataSource: UsersPagingDataSource
    private var startWithQuery = ""
    val usersLiveData = Pager(config = PagingConfig(10, enablePlaceholders = false),
        pagingSourceFactory = {
            currentUserDataSource = UsersPagingDataSource(firestoreRepo, startWithQuery)
            currentUserDataSource
        }
    ).liveData.cachedIn(viewModelScope)
    val gotoMessageActivityLiveData = MutableLiveData<String?>()

    fun searchUsers(startWith: String) {
        startWithQuery = startWith
        currentUserDataSource.invalidate()
    }

    fun initiateConversation(userId: String) {
        gotoMessageActivityLiveData.postValue(userId)
    }
}
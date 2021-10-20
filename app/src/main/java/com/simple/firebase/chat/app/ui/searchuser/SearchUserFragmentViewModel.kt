package com.simple.firebase.chat.app.ui.searchuser

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.simple.firebase.chat.app.datasource.pagingsource.UsersPagingDataSource
import com.simple.firebase.chat.app.datasource.repo.FirebaseRepo
import com.simple.firebase.chat.app.config.Config
class SearchUserFragmentViewModel(private val firebaseRepo: FirebaseRepo) : ViewModel() {
    private lateinit var currentUserDataSource: UsersPagingDataSource
    private var startWithQuery = ""
    val usersLiveData = Pager(config = PagingConfig(Config.USERS_LIST_SIZE.toInt(), enablePlaceholders = false),
        pagingSourceFactory = {
            currentUserDataSource = UsersPagingDataSource(firebaseRepo, startWithQuery)
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
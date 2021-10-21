package com.simple.firebase.chat.app.ui.account

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.simple.firebase.chat.app.datasource.repo.FirebaseRepo
import com.simple.firebase.chat.app.model.User
import com.simple.firebase.chat.app.util.MainUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountFragmentViewModel(private val firebaseRepo: FirebaseRepo) : ViewModel() {

    val showEmailLiveData = MutableLiveData<String?>()
    val changeNameLiveData = MutableLiveData<String?>()
    val nameChangedSuccessful = MutableLiveData(false)
    val signOutSuccessLiveData = MutableLiveData(false)
    val signFailedLiveData = MutableLiveData<Exception?>()
    private var nameFetched = false

    fun changeName(newName: String) {
        if (newName.trim().isNotEmpty()) {
            CoroutineScope(Dispatchers.Unconfined).launch {
                firebaseRepo.updateOwnAccountName(newName, {
                    nameChangedSuccessful.postValue(true)
                }, null)
            }
        }
    }

    fun getEmailIfNotYet() {
        if (showEmailLiveData.value == null) showEmailLiveData.postValue(firebaseRepo.email)
    }

    fun getNameIfNotYet() {
        if (!nameFetched) {
            nameFetched = true
            val onSuccess = { user: User? ->
                changeNameLiveData.postValue(user?.name)
            }

            val onFailure = { e: Exception ->

            }
            firebaseRepo.getUserViaSnapshotWithUserId(firebaseRepo.userId, onSuccess, onFailure)
        }
    }


    fun logoutAccount(context: Context, webClientId: String) {
        MainUtil.buildGoogleSignInClient(context, webClientId).signOut().addOnSuccessListener {
            firebaseRepo.logout()
            signOutSuccessLiveData.postValue(true)
        }.addOnFailureListener {
            signFailedLiveData.postValue(it)
        }
    }
}
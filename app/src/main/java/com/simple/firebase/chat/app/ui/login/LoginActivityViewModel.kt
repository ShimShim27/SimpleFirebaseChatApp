package com.simple.firebase.chat.app.ui.login

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.simple.firebase.chat.app.datasource.repo.FirebaseRepo
import com.simple.firebase.chat.app.util.MainUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivityViewModel(private val firebaseRepo: FirebaseRepo) : ViewModel() {

    val notLoginLiveData = MutableLiveData(false)
    val launchGoogleLoginLiveData = MutableLiveData<Intent?>()
    val signFailedLiveData = MutableLiveData<Exception?>()
    val signInSuccessLiveData = MutableLiveData(false)


    fun checkIfSignedIn() {
        if (firebaseRepo.userId.isNotEmpty()) signInSuccessLiveData.postValue(true)
        else notLoginLiveData.postValue(true)
    }

    fun launchGoogleLogin(context: Context, webClientId: String) {
        launchGoogleLoginLiveData.postValue(
            MainUtil.buildGoogleSignInClient(
                context,
                webClientId
            ).signInIntent
        )
    }


    fun onGoogleLoginResult(context: Context, webClientId: String, activityResult: ActivityResult) {
        when (activityResult.resultCode) {
            -1 -> {
                CoroutineScope(Dispatchers.Unconfined).launch {
                    val account =
                        GoogleSignIn.getSignedInAccountFromIntent(activityResult.data).result
                    val onSuccess = { signInSuccessLiveData.postValue(true) }

                    val onFailure = { e: Exception ->
                        MainUtil.buildGoogleSignInClient(
                            context,
                            webClientId
                        ).signOut().addOnCompleteListener {
                            signFailedLiveData.postValue(e)
                        }
                        Unit

                    }
                    firebaseRepo.loginViaGoogle(account, onSuccess, onFailure)
                }
            }

            else -> signFailedLiveData.postValue(Exception(""))
        }
    }


}
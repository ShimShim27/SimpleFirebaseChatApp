package com.simple.firebase.chat.app.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.simple.firebase.chat.app.R
import com.simple.firebase.chat.app.ui.main.MainActivity
import com.simple.firebase.chat.app.util.MainUtil

class LoginActivity : AppCompatActivity() {
    private lateinit var launchGoogleLogin: ActivityResultLauncher<Intent>
    private lateinit var viewModel: LoginActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        launchGoogleLogin =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                viewModel.onGoogleLoginResult(this, getString(R.string.default_web_client_id), it)
            }

        initViewModel()
        viewModel.checkIfSignedIn()

    }

    private fun initViewModel() {
        viewModel = MainUtil.getViewModuleComponent(this).createLoginActivityViewModel()
        viewModel.launchGoogleLoginLiveData.observe(this, {
            if (it != null) {
                viewModel.launchGoogleLoginLiveData.value = null
                launchGoogleLogin.launch(it)
            }
        })

        viewModel.signFailedLiveData.observe(this, {
            if (it != null) {
                viewModel.signFailedLiveData.value = null
                val message = it.message
                MainUtil.makeToast(
                    this,
                    if (message?.isEmpty() == false) getString(R.string.login_failed_message) else message
                )
            }
        })

        viewModel.signInSuccessLiveData.observe(this, {
            if (it) {
                viewModel.signInSuccessLiveData.value = false
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })
    }

    fun onLoginButtonClicked(v: View) {
        viewModel.launchGoogleLogin(this, getString(R.string.default_web_client_id))
    }
}
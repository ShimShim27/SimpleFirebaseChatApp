package com.simple.firebase.chat.app.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.simple.firebase.chat.app.R
import com.simple.firebase.chat.app.ui.main.MainActivity
import com.simple.firebase.chat.app.util.MainUtil

class LoginActivity : AppCompatActivity() {
    private lateinit var launchGoogleLogin: ActivityResultLauncher<Intent>
    private lateinit var viewModel: LoginActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        initViewModel()
        launchGoogleLogin =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                viewModel.onGoogleLoginResult(
                    this,
                    getString(R.string.default_web_client_id),
                    result
                )
            }

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

        viewModel.notLoginLiveData.observe(this, {
            if (it) {
                viewModel.notLoginLiveData.value = false
                setContentView(R.layout.activity_login)
                supportActionBar?.show()
            }
        })
    }

    fun onLoginButtonClicked(v: View) {
        viewModel.launchGoogleLogin(this, getString(R.string.default_web_client_id))
    }
}
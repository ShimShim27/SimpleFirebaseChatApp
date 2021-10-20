package com.simple.firebase.chat.app.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.simple.firebase.chat.app.R
import com.simple.firebase.chat.app.custom.BaseDialogFragment
import android.view.ViewGroup.LayoutParams
import android.widget.EditText
import android.widget.TextView
import com.simple.firebase.chat.app.ui.login.LoginActivity
import com.simple.firebase.chat.app.util.MainUtil

class AccountFragment : BaseDialogFragment(
    R.layout.account_fragment_layout,
    LayoutParams.MATCH_PARENT,
    LayoutParams.WRAP_CONTENT
) {

    private lateinit var viewModel: AccountFragmentViewModel
    private lateinit var nameInputBox: EditText
    private lateinit var updateNameButton: View
    private lateinit var logoutButton: View
    private lateinit var emailDisplay: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            nameInputBox = findViewById(R.id.nameInputBox)
            updateNameButton = findViewById(R.id.updateNameButton)
            logoutButton = findViewById(R.id.logoutButton)
            emailDisplay = findViewById(R.id.emailDisplay)
        }

        initViewModel()

        updateNameButton.setOnClickListener {
            viewModel.changeName(nameInputBox.text.toString())
        }

        logoutButton.setOnClickListener {
            viewModel.logoutAccount(requireContext(), getString(R.string.default_web_client_id))
        }

        viewModel.getNameIfNotYet()
        viewModel.getEmailIfNotYet()
    }


    private fun initViewModel() {
        viewModel =
            MainUtil.getViewModuleComponent(requireContext()).createAccountFragmentViewModel()

        viewModel.changeNameLiveData.observe(viewLifecycleOwner, {
            if (it != null) nameInputBox.setText(it)
        })

        viewModel.signOutSuccessLiveData.observe(viewLifecycleOwner, {
            if (it) {
                viewModel.signOutSuccessLiveData.value = false
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
        })

        viewModel.signFailedLiveData.observe(viewLifecycleOwner, {
            if (it != null) {
                viewModel.signFailedLiveData.value = null
                MainUtil.makeToast(requireContext(), it.message.toString())
            }
        })

        viewModel.showEmailLiveData.observe(viewLifecycleOwner, {
            if (it != null) emailDisplay.text = it
        })

        viewModel.nameChangedSuccessful.observe(viewLifecycleOwner, {
            if (it) {
                viewModel.nameChangedSuccessful.value = false
                dismiss()
            }
        })
    }
}
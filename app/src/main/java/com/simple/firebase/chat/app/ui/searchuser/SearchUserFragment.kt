package com.simple.firebase.chat.app.ui.searchuser

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.simple.firebase.chat.app.R
import com.simple.firebase.chat.app.custom.BaseDialogFragment
import com.simple.firebase.chat.app.ui.messages.MessagesActivity
import com.simple.firebase.chat.app.util.MainUtil

class SearchUserFragment :
    BaseDialogFragment(R.layout.search_user_fragment, MATCH_PARENT, MATCH_PARENT) {

    private lateinit var viewModel: SearchUserFragmentViewModel
    private lateinit var searchUserRecyclerAdapter: SearchUserRecyclerAdapter
    private lateinit var userRecyclerList: RecyclerView
    private lateinit var searchUserInputBox: EditText
    private lateinit var searchButton: View


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            userRecyclerList = findViewById(R.id.userRecyclerList)
            searchUserInputBox = findViewById(R.id.searchUserInputBox)
            searchButton = findViewById(R.id.searchButton)
        }



        initViewModel()

        searchUserRecyclerAdapter = SearchUserRecyclerAdapter(viewModel)
        searchButton.setOnClickListener {
            val query = searchUserInputBox.text.toString()
            if (query.trim().isNotEmpty()) viewModel.searchUsers(query)
        }

        userRecyclerList.adapter = searchUserRecyclerAdapter


    }


    private fun initViewModel() {
        viewModel =
            MainUtil.getViewModuleComponent(requireContext()).createSearchFragmentUserViewModel()

        viewModel.usersLiveData.observe(this, {
            searchUserRecyclerAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        })

        viewModel.gotoMessageActivityLiveData.observe(this, { user ->
            if (user != null) {
                viewModel.gotoMessageActivityLiveData.value = null
                startActivity(Intent(requireContext(), MessagesActivity::class.java).apply {
                    putExtra(MessagesActivity.EXTRA_OTHER_USER_ID, user.id)
                    putExtra(MessagesActivity.EXTRA_OTHER_USER_NAME, user.name)
                })
            }
        })
    }


}
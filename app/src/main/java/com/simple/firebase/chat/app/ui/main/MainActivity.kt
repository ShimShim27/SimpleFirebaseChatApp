package com.simple.firebase.chat.app.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.simple.firebase.chat.app.R
import com.simple.firebase.chat.app.ui.account.AccountFragment
import com.simple.firebase.chat.app.ui.messages.MessagesActivity
import com.simple.firebase.chat.app.ui.searchuser.SearchUserFragment
import com.simple.firebase.chat.app.util.MainUtil

class MainActivity : AppCompatActivity() {

    private lateinit var conversationRecyclerAdapter: ConversationRecyclerAdapter
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var viewModel: MainActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messagesRecyclerView = findViewById(R.id.conversationsRecyclerBox)

        initViewModel()
        viewModel.setConversationUpdateListener()
        conversationRecyclerAdapter = ConversationRecyclerAdapter(viewModel)
        messagesRecyclerView.adapter = conversationRecyclerAdapter
        messagesRecyclerView.addItemDecoration(ConversationRecyclerDecoration())

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.account -> AccountFragment().show(supportFragmentManager, null)
        }
        return true
    }


    private fun initViewModel() {
        viewModel = MainUtil.getViewModuleComponent(this).createMainActivityViewModel()
        viewModel.conversationsLiveData.observe(this, {
            conversationRecyclerAdapter.submitData(lifecycle, it)
        })

        viewModel.gotoMessagesLiveData.observe(this, {
            if (it != null) {
                viewModel.gotoMessagesLiveData.value = null
                startActivity(Intent(this, MessagesActivity::class.java).apply {
                    putExtra(MessagesActivity.EXTRA_OTHER_USER_ID, it.partnerUserId)
                    putExtra(MessagesActivity.EXTRA_OTHER_USER_NAME, it.name)
                })
            }

        })
    }

    fun onClickSearchUser(v: View) {
        SearchUserFragment().show(supportFragmentManager, null)
    }
}
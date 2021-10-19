package com.simple.firebase.chat.app.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.paging.cachedIn
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import com.simple.firebase.chat.app.R
import com.simple.firebase.chat.app.dagger.DaggerViewModelComponent
import com.simple.firebase.chat.app.dagger.ViewModelModule
import com.simple.firebase.chat.app.ui.messages.MessagesActivity
import com.simple.firebase.chat.app.util.MainUtil
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

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
                    putExtra(MessagesActivity.EXTRA_OTHER_USER_ID, it)
                })
            }

        })
    }
}
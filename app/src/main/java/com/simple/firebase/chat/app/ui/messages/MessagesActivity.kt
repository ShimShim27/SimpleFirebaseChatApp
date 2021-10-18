package com.simple.firebase.chat.app.ui.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simple.firebase.chat.app.R
import com.simple.firebase.chat.app.util.MainUtil

class MessagesActivity : AppCompatActivity() {
    private lateinit var messagesRecyclerAdapter: MessagesRecyclerAdapter
    private lateinit var viewModel: MessagesActivityViewModel
    private lateinit var messagesRecyclerView: RecyclerView

    companion object {
        const val EXTRA_OTHER_USER_ID = "other user id"
    }

    private var otherUserId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        otherUserId = intent.extras!!.getString(EXTRA_OTHER_USER_ID)!!

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        initViewModel()

        messagesRecyclerAdapter = MessagesRecyclerAdapter()
        messagesRecyclerView.adapter = messagesRecyclerAdapter

        viewModel.getMessagesIfNotYet(otherUserId)
    }

    private fun initViewModel() {
        viewModel = MainUtil.getViewModuleComponent(this).createMessagesActivityViewModel()
        viewModel.messagesLiveData.observe(this, {
            messagesRecyclerAdapter.submitList(ArrayList(it))
        })

    }
}
package com.simple.firebase.chat.app.ui.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simple.firebase.chat.app.R
import com.simple.firebase.chat.app.util.MainUtil

class MessagesActivity : AppCompatActivity() {
    private lateinit var messagesRecyclerAdapter: MessagesRecyclerAdapter
    private lateinit var messageInputText: TextView
    private lateinit var viewModel: MessagesActivityViewModel
    private lateinit var messagesRecyclerView: RecyclerView

    companion object {
        const val EXTRA_OTHER_USER_ID = "other user id"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)



        messageInputText = findViewById(R.id.messageInputText)
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        initViewModel()

        viewModel.otherUserId = intent.extras!!.getString(EXTRA_OTHER_USER_ID)!!

        messagesRecyclerAdapter = MessagesRecyclerAdapter(viewModel)
        messagesRecyclerView.adapter = messagesRecyclerAdapter

        viewModel.setMessagesUpdateListener()
    }


    private fun initViewModel() {
        viewModel = MainUtil.getViewModuleComponent(this).createMessagesActivityViewModel()
        viewModel.messagesLiveData.observe(this, {
            messagesRecyclerAdapter.submitData(lifecycle, it)
            /* messagesRecyclerView.post {
                 messagesRecyclerView.scrollToPosition(it.size - 1)
             }*/

        })

    }

    fun onClickSend(v: View) {
        messageInputText.text.toString().apply {
            if (this.trim().isNotEmpty()) {
                viewModel.sendMessage(this)
                messageInputText.text = ""
            }
        }

    }
}
package com.simple.firebase.chat.app.ui.messages

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.simple.firebase.chat.app.R
import com.simple.firebase.chat.app.model.Message

class MessagesRecyclerAdapter(private val viewModel: MessagesActivityViewModel) :
    ListAdapter<Message, MessagesRecyclerAdapter.CustomViewHolder>(callback) {

    companion object {
        val callback = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem.id == newItem.id &&
                        oldItem.message == newItem.message &&
                        oldItem.sender == newItem.sender &&
                        oldItem.receiver == newItem.receiver &&
                        oldItem.date == newItem.date

        }
    }


    inner class CustomViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val messageView: TextView = v.findViewById(R.id.messageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.messages_recycler_box, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val message = getItem(position)
        holder.messageView.apply {
            text = message.message
            gravity = if (message.sender == viewModel.getUserId()) Gravity.END else Gravity.START
        }

    }
}
package com.simple.firebase.chat.app.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.simple.firebase.chat.app.R
import com.simple.firebase.chat.app.model.Conversation

class ConversationRecyclerAdapter(private val viewModel: MainActivityViewModel) :
    ListAdapter<Conversation, ConversationRecyclerAdapter.CustomViewHolder>(callback) {


    companion object {
        val callback = object : DiffUtil.ItemCallback<Conversation>() {
            override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean =
                oldItem.partnerUserId == newItem.partnerUserId

            override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean =
                oldItem.partnerUserId == newItem.partnerUserId

        }
    }

    inner class CustomViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val otherUserId: TextView = v.findViewById(R.id.otherUserId)

        init {
            v.setOnClickListener { viewModel.gotoMessages(getItem(adapterPosition).partnerUserId) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.conversation_recycler_box, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val conversation = getItem(position)
        holder.otherUserId.text = conversation.partnerUserId
    }
}
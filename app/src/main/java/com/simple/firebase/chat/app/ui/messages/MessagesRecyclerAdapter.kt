package com.simple.firebase.chat.app.ui.messages

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.simple.firebase.chat.app.R
import com.simple.firebase.chat.app.model.Message
import com.simple.firebase.chat.app.util.MainUtil

class MessagesRecyclerAdapter(private val viewModel: MessagesActivityViewModel) :
    PagingDataAdapter<Message, MessagesRecyclerAdapter.CustomViewHolder>(callback) {

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


    inner class CustomViewHolder(val v: LinearLayout) : RecyclerView.ViewHolder(v) {
        val messageView: TextView = v.findViewById(R.id.messageView)
        val messageContainerCardView: CardView = v.findViewById(R.id.messageContainerCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.messages_recycler_box, parent, false) as LinearLayout
        )
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val message = getItem(position)!!
        val isSender = message.sender == viewModel.getUserId()
        val context = holder.v.context
        val sidePadding = 100

        holder.messageView.text = message.message
        holder.v.gravity = if (isSender) Gravity.END else Gravity.START
        holder.v.setPadding(
            if (isSender) sidePadding else 0,
            0,
            if (isSender) 0 else sidePadding,
            0
        )
        holder.messageContainerCardView.setCardBackgroundColor(
            MainUtil.getColorFromAttr(
                context,
                if (isSender) R.attr.colorPrimary else R.attr.rippleColor
            )
        )

    }
}
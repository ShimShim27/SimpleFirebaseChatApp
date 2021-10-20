package com.simple.firebase.chat.app.ui.searchuser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.simple.firebase.chat.app.R
import com.simple.firebase.chat.app.model.User
import com.simple.firebase.chat.app.ui.messages.MessagesRecyclerAdapter

class SearchUserRecyclerAdapter(private val viewModel: SearchUserFragmentViewModel) :
    PagingDataAdapter<User, SearchUserRecyclerAdapter.CustomViewHolder>(callback) {

    companion object {
        val callback = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.id == newItem.id &&
                        oldItem.name == newItem.name
        }
    }

    inner class CustomViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val userName: TextView = v.findViewById(R.id.userName)

        init {
            v.setOnClickListener { viewModel.initiateConversation(getItem(bindingAdapterPosition)!!.id) }
        }
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val user = getItem(position)!!
        holder.userName.text = user.name
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder {
        return CustomViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.searh_user_recycler_box, parent, false)
        )
    }
}
package com.simple.firebase.chat.app.ui.searchuser

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.simple.firebase.chat.app.R
import com.simple.firebase.chat.app.model.User
import com.simple.firebase.chat.app.util.MainUtil

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

    inner class CustomViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
        val userName: TextView = v.findViewById(R.id.userName)
        val profileImageView: ImageView = v.findViewById(R.id.profileImageView)

        init {
            v.setOnClickListener { viewModel.initiateConversation(getItem(bindingAdapterPosition)!!) }
        }
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val user = getItem(position)!!
        holder.userName.text = user.name
        MainUtil.loadImageFromUrl(
            holder.v.context,
            holder.profileImageView,
            Uri.parse(MainUtil.getRandomAvatarLink())
        )
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
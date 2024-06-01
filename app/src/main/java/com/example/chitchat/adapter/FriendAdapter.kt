package com.example.chitchat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.R
import com.example.chitchat.model.Friend

class FriendAdapter(private val context: Context, private val friendList: List<Friend>) :
    RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.listview_item, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friendList[position]
        holder.nickName.text = friend.nickName
        holder.profileImage.setImageResource(friend.profileImageResId) // If using a drawable resource ID
        // Alternatively, if using an image URL with a library like Glide:
        // Glide.with(context).load(friend.profileImageUrl).into(holder.profileImage)
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.listProfileArea)
        val nickName: TextView = itemView.findViewById(R.id.listNickNameArea)
    }
}

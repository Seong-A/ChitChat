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

class FriendAdapter(
    private val context: Context,
    private val friends: List<Friend>
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.listview_item, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]
        holder.nameTextView.text = friend.name
        holder.profileImageView.setImageResource(friend.profileImage)
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.listNickNameArea)
        val profileImageView: ImageView = itemView.findViewById(R.id.listProfileArea)

        init {
            itemView.setOnClickListener {
                listener?.onItemClick(adapterPosition)
            }
        }
    }
}

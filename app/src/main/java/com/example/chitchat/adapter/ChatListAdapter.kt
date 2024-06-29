package com.example.chitchat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.R
import com.example.chitchat.model.Chat

class ChatListAdapter(private val context: Context, private val chats: List<Chat>, private val listener: OnChatClickListener) :
    RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    interface OnChatClickListener {
        fun onChatClick(chat: Chat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.listview_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]
        holder.bind(chat)
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.listProfileArea)
        private val nameText: TextView = itemView.findViewById(R.id.listNickNameArea)
        private val lastMessageText: TextView = itemView.findViewById(R.id.text)

        fun bind(chat: Chat) {
            profileImage.setImageResource(chat.profileImageResId)
            nameText.text = chat.name

            val recentMessage = chat.recentMessage
            if (recentMessage != null) {
                lastMessageText.visibility = View.VISIBLE
                lastMessageText.text = recentMessage.text
            } else {
                lastMessageText.visibility = View.GONE
            }

            // 아이템 클릭 처리
            itemView.setOnClickListener {
                listener.onChatClick(chat)
            }
        }
    }
}

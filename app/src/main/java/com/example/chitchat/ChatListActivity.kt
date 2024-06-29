package com.example.chitchat

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.adapter.ChatListAdapter
import com.example.chitchat.model.Chat
import com.example.chitchat.model.Message
import com.example.chitchat.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatListActivity : AppCompatActivity(), ChatListAdapter.OnChatClickListener {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatListAdapter
    private lateinit var chatList: MutableList<Chat>
    private lateinit var database: DatabaseReference
    private lateinit var currentUserEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatlist)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        database = FirebaseDatabase.getInstance().reference

        chatList = mutableListOf()
        chatAdapter = ChatListAdapter(this, chatList, this)
        chatRecyclerView.adapter = chatAdapter

        val addChatButton: ImageView = findViewById(R.id.addChat)
        addChatButton.setOnClickListener {
            val intent = Intent(this, SearchFriendActivity::class.java)
            startActivity(intent)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val homeintent = Intent(this, MainActivity::class.java)
                    startActivity(homeintent)
                    finish()
                    true
                }
                R.id.chat -> {
                    true
                }
                R.id.botchat -> {
                    val botintent = Intent(this, BotChatActivity::class.java)
                    startActivity(botintent)
                    finish()
                    true
                }
                R.id.mypage -> {
                    val mypageintent = Intent(this, MypageActivity::class.java)
                    startActivity(mypageintent)
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.chat

        currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        loadChatList()
    }

    private fun loadChatList() {
        val messagesRef = database.child("messages")
        messagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatMap = mutableMapOf<String, Chat>()

                for (messageSnapshot in snapshot.children) {
                    val receiver = messageSnapshot.child("receiver").getValue(String::class.java) ?: ""
                    val sender = messageSnapshot.child("sender").getValue(String::class.java) ?: ""
                    val text = messageSnapshot.child("text").getValue(String::class.java) ?: ""
                    val timestamp = messageSnapshot.child("timestamp").getValue(Long::class.java) ?: 0

                    val message = Message(text, sender, receiver, timestamp)

                    if (receiver == currentUserEmail || sender == currentUserEmail) {
                        val chatPartner = if (receiver == currentUserEmail) sender else receiver
                        val chat = chatMap.getOrPut(chatPartner) {
                            Chat(chatPartner, 0, mutableListOf(), null)
                        }
                        chat.messages.add(message)
                        if (chat.recentMessage == null || message.timestamp > chat.recentMessage!!.timestamp) {
                            chat.recentMessage = message
                        }
                    }
                }

                chatList.clear()
                chatList.addAll(chatMap.values)
                chatList.sortByDescending { it.recentMessage?.timestamp }
                chatAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류 처리
            }
        })
    }

    override fun onChatClick(chat: Chat) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("friendEmail", chat.name)
        startActivity(intent)
    }
}

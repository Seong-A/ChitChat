package com.example.chitchat

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.adapter.MessageAdapter
import com.example.chitchat.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etMsg: EditText
    private lateinit var btnSend: ImageButton
    private val messages = mutableListOf<Message>()
    private lateinit var adapter: MessageAdapter

    private lateinit var database: DatabaseReference
    private lateinit var currentUserEmail: String
    private lateinit var friendEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerView = findViewById(R.id.recycler_view)
        etMsg = findViewById(R.id.et_msg)
        btnSend = findViewById(R.id.btn_send)

        currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        friendEmail = intent.getStringExtra("friendEmail") ?: ""

        supportActionBar?.title = friendEmail

        adapter = MessageAdapter(messages, currentUserEmail)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance().reference

        btnSend.setOnClickListener {
            val msg = etMsg.text.toString().trim()
            if (msg.isNotEmpty()) {
                sendMessage(msg)
                etMsg.text.clear()
            }
        }

        loadMessages()
    }

    private fun sendMessage(message: String) {
        val msg = Message(message, currentUserEmail, friendEmail, System.currentTimeMillis())
        val msgKey = database.child("messages").push().key
        if (msgKey != null) {
            database.child("messages").child(msgKey).setValue(msg)
        }
    }

    private fun loadMessages() {
        val messagesRef = database.child("messages")
        messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val msg = dataSnapshot.getValue(Message::class.java)
                if (msg != null && ((msg.sender == currentUserEmail && msg.receiver == friendEmail) ||
                            (msg.sender == friendEmail && msg.receiver == currentUserEmail))) {
                    messages.add(msg)
                    adapter.notifyItemInserted(messages.size - 1)
                    recyclerView.scrollToPosition(messages.size - 1)
                }
            }


            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}

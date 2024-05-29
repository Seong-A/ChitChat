package com.example.chitchat

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.adapter.MessageAdapter
import com.example.chitchat.model.Message

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etMsg: EditText
    private lateinit var btnSend: ImageButton
    private val messages = mutableListOf<Message>()
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerView = findViewById(R.id.recycler_view)
        etMsg = findViewById(R.id.et_msg)
        btnSend = findViewById(R.id.btn_send)

        adapter = MessageAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnSend.setOnClickListener {
            val msg = etMsg.text.toString().trim()
            if (msg.isNotEmpty()) {
                // Add user message
                messages.add(Message(msg, true))
                // Simulate bot response
                messages.add(Message("Bot reply to: $msg", false))
                adapter.notifyDataSetChanged()
                etMsg.text.clear()
                recyclerView.scrollToPosition(messages.size - 1)
            }
        }
    }
}

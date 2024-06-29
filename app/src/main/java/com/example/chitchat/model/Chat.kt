package com.example.chitchat.model

data class Chat(
    val name: String,
    val profileImageResId: Int,
    val messages: MutableList<Message>,
    var recentMessage: Message?
)

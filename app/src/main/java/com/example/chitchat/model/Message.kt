package com.example.chitchat.model

data class Message(
    val text: String = "",
    val sender: String = "",
    val receiver: String = "",
    val timestamp: Long = 0
)

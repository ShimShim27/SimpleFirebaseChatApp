package com.simple.firebase.chat.app.model

data class Message(
    val id: String,
    val message: String,
    val otherUserId: String,
    val received: Boolean
)
package com.simple.firebase.chat.app.model

import java.util.*

data class Message(
    val id: String,
    val message: String,
    val sender: String,
    val receiver: String,
    val date: Date
)
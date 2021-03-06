package com.simple.firebase.chat.app.constants

object FirestoreStructure {
    interface FirestoreColumn {
        val name: String
    }

    class Messages : FirestoreColumn {
        override val name: String = "messages"
        val date: String = "date"
        val message: String = "message"
        val senderReceiver: String = "sender_receiver"
    }

    class Conversations : FirestoreColumn {
        override val name: String = "conversations"
        val owner = "owner"
        val contacts = "contacts"
        val date = "date"
    }

    class Users : FirestoreColumn {
        override val name: String = "users"
        val nameField = "name"
    }

}
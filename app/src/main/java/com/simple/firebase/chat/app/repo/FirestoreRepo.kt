package com.simple.firebase.chat.app.repo

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.simple.firebase.chat.app.model.Conversation
import com.simple.firebase.chat.app.model.Message
import java.lang.Exception
import javax.inject.Inject

class FirestoreRepo @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()


    fun getConversations(
        onSuccess: ((messages: List<Conversation>) -> Unit)?,
        onFailure: ((e: Exception) -> Unit)?
    ) {
        firestore.collection("users/UserId/conversations").get()
            .addOnSuccessListener {

                onSuccess?.invoke(it.documents.map { doc ->
                    Conversation(
                        doc.id
                    )
                })


            }.addOnFailureListener {
                onFailure?.invoke(it)
            }
    }


    fun getMessages(
        otherUserId: String,
        onSuccess: ((messages: List<Message>) -> Unit)?,
        onFailure: ((e: Exception) -> Unit)?
    ) {
        firestore.collection("users/UserId/messages").whereEqualTo("otherUserId", otherUserId).get()
            .addOnSuccessListener {
                onSuccess?.invoke(it.documents.map { doc ->
                    Message(
                        doc.id,
                        doc.getString("message")!!,
                        doc.getString("otherUserId")!!,
                        doc.getBoolean("received")!!
                    )
                })
            }.addOnFailureListener {
            onFailure?.invoke(it)
        }
    }
}
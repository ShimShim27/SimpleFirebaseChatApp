package com.simple.firebase.chat.app.repo

import android.util.Log
import com.google.firebase.firestore.*
import com.simple.firebase.chat.app.model.Conversation
import com.simple.firebase.chat.app.model.Message
import com.simple.firebase.chat.app.structure.FirestoreStructure
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

class FirestoreRepo @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val messagesColumn = FirestoreStructure.Messages()
    private val conversationsColumn = FirestoreStructure.Conversations()
    val userId = "UserId1"


    fun getConversations(
        onSuccess: ((messages: List<Conversation>) -> Unit)?,
        onFailure: ((e: Exception) -> Unit)?
    ): EventListener<QuerySnapshot> {

        val listener = object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    onFailure?.invoke(error)
                    return
                }

                val conversations = mutableListOf<Conversation>()
                value?.documents?.forEach { doc ->
                    conversations.add(Conversation(doc.getString(conversationsColumn.partnerUserId)!!))
                }
                onSuccess?.invoke(conversations)

            }

        }
        firestore.collection(conversationsColumn.name)
            .whereEqualTo(conversationsColumn.owner, userId)
            .addSnapshotListener(listener)

        return listener


    }


    fun getMessages(
        otherUserId: String,
        onSuccess: ((messages: List<Message>) -> Unit)?,
        onFailure: ((e: Exception) -> Unit)?
    ): EventListener<QuerySnapshot> {

        val listener = object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    onFailure?.invoke(error)
                    return
                }

                val messages = mutableListOf<Message>()
                value?.documents?.forEach { doc ->
                    try {
                        val senderReceiver =
                            doc.getString(messagesColumn.senderReceiver)!!.split(":")

                        messages.add(
                            Message(
                                doc.id,
                                doc.getString(messagesColumn.message)!!,
                                senderReceiver[0],
                                senderReceiver[1],
                                doc.getDate(messagesColumn.date)!!
                            )
                        )
                    } catch (e: Exception) {
                    }
                }
                onSuccess?.invoke(messages)

            }

        }

        firestore.collection(messagesColumn.name).whereIn(
            messagesColumn.senderReceiver,
            listOf("$userId:$otherUserId", "$otherUserId:$userId")
        ).orderBy(messagesColumn.date, Query.Direction.DESCENDING).addSnapshotListener(listener)

        return listener
    }


    fun sendMessage(
        targetId: String,
        message: String,
        onSuccess: (() -> Unit)?,
        onFailure: ((e: Exception) -> Unit)?
    ) {
        val data = mapOf(
            messagesColumn.date to FieldValue.serverTimestamp(),
            messagesColumn.message to message,
            messagesColumn.senderReceiver to "$userId:$targetId"
        )
        firestore.collection(messagesColumn.name).add(data).addOnSuccessListener {
            onSuccess?.invoke()
        }.addOnFailureListener {
            onFailure?.invoke(it)
        }
    }
}
package com.simple.firebase.chat.app.repo

import android.util.Log
import com.google.firebase.firestore.*
import com.simple.firebase.chat.app.model.Conversation
import com.simple.firebase.chat.app.model.Message
import com.simple.firebase.chat.app.structure.FirestoreStructure
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

class FirestoreRepo @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val messagesColumn = FirestoreStructure.Messages()
    private val conversationsColumn = FirestoreStructure.Conversations()
    val userId = "UserId1"


    fun getConversations(
        limit: Long,
        fromSnapshot: DocumentSnapshot?,
        onSuccess: ((lastDocumentSnapshot: DocumentSnapshot?, messages: List<Conversation>) -> Unit)?,
        onFailure: ((e: Exception) -> Unit)?
    ) {


        val listener = object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    onFailure?.invoke(error)
                    return
                }

                var lastDocumentSnapshot: DocumentSnapshot? = null
                val conversations = mutableListOf<Conversation>()

                value?.documents?.forEach { doc ->
                    try {
                        val contacts = doc.get(conversationsColumn.contacts) as List<String>
                        conversations.add(Conversation(if (contacts[0] == userId) contacts[1] else contacts[0]))
                        lastDocumentSnapshot = doc
                    } catch (e: Exception) {

                    }
                }
                onSuccess?.invoke(lastDocumentSnapshot, conversations)

            }

        }

        var query = firestore.collection(conversationsColumn.name)
            .orderBy(conversationsColumn.date, Query.Direction.DESCENDING)
            .whereEqualTo(conversationsColumn.owner, userId)
            .limit(limit)
        query = if (fromSnapshot == null) query else query.startAfter(fromSnapshot)
        query.addSnapshotListener(listener)


    }


    fun getMessages(
        limit: Long,
        fromSnapshot: DocumentSnapshot?,
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
                        val senderReceiver = doc.get(messagesColumn.senderReceiver) as List<String>

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

        var query = firestore.collection(messagesColumn.name)
            .whereIn(messagesColumn.senderReceiver, listOf(listOf(userId, otherUserId)))
            .orderBy(messagesColumn.date, Query.Direction.ASCENDING)
            .limit(limit)
        query = if (fromSnapshot == null) query else query.startAfter(fromSnapshot)
        query.addSnapshotListener(listener)

        return listener
    }


    suspend fun sendMessage(
        targetId: String,
        message: String,
        onSuccess: (() -> Unit)?,
        onFailure: ((e: Exception) -> Unit)?
    ) {


        try {
            val messageData = mapOf(
                messagesColumn.date to FieldValue.serverTimestamp(),
                messagesColumn.message to message,
                messagesColumn.senderReceiver to listOf(userId, targetId)
            )

            firestore.collection(messagesColumn.name).add(messageData).await()

            val conversationCol = firestore.collection(conversationsColumn.name)
            val conversationQuery = { whichId: String ->
                conversationCol.whereEqualTo(conversationsColumn.owner, whichId)
                    .whereIn(conversationsColumn.contacts, listOf(listOf(userId, targetId)))
                    .get()
            }


            val myConversationSnapshot = conversationQuery(userId).await()
            val otherUserConversationSnapshot = conversationQuery(targetId).await()

            val newConversationData = { whichId: String ->
                mapOf(
                    conversationsColumn.owner to whichId,
                    conversationsColumn.date to FieldValue.serverTimestamp(),
                    conversationsColumn.contacts to listOf(userId, targetId)
                )
            }

            myConversationSnapshot.documents.also {
                val newData = newConversationData(userId)
                if (it.isEmpty()) conversationCol.add(newData).await()
                else it.forEach { doc -> doc.reference.update(newData).await() }
            }


            otherUserConversationSnapshot.documents.also {
                val newData = newConversationData(targetId)
                if (it.isEmpty()) conversationCol.add(newData).await()
                else it.forEach { doc -> doc.reference.update(newData).await() }
            }

            onSuccess?.invoke()

        } catch (e: Exception) {
            onFailure?.invoke(e)
        }


    }


}
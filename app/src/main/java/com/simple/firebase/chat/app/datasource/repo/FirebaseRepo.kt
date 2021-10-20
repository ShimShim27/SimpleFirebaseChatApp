package com.simple.firebase.chat.app.datasource.repo

import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.simple.firebase.chat.app.config.Config
import com.simple.firebase.chat.app.model.Conversation
import com.simple.firebase.chat.app.model.Message
import com.simple.firebase.chat.app.model.User
import com.simple.firebase.chat.app.structure.FirestoreStructure
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.lang.reflect.Field
import java.util.*
import javax.inject.Inject

class FirebaseRepo @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val messagesColumn = FirestoreStructure.Messages()
    private val conversationsColumn = FirestoreStructure.Conversations()
    private val usersColumn = FirestoreStructure.Users()

    val userId: String
        get() {
            return auth.currentUser?.uid ?: ""
        }

    val email: String
        get() {
            return auth.currentUser?.email ?: ""
        }


    suspend fun loginViaGoogle(
        account: GoogleSignInAccount,
        onSuccess: (() -> Unit)?,
        onFailure: ((e: Exception) -> Unit)?
    ) {

        try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).await()

            /**
             * Check if user account
             * data exists in firestore. If not
             * then create one.
             */
            val querySnapshot =
                firestore.collection(usersColumn.name).whereEqualTo(FieldPath.documentId(), userId)
                    .get().await()
            var user: User? = null
            querySnapshot.documents.apply {
                if (isNotEmpty()) {
                    try {
                        val doc = get(0)
                        user = User(doc.id, doc.getString(usersColumn.nameField)!!)
                    } catch (e: Exception) {
                    }
                }
            }

            if (user != null) onSuccess?.invoke()
            else {
                val data = mapOf(usersColumn.nameField to "User ${Random().nextInt()}")
                firestore.collection(usersColumn.name).document(userId).set(data).await()
                onSuccess?.invoke()
            }

        } catch (e: Exception) {
            auth.signOut()
            onFailure?.invoke(e)
        }

    }

    fun logout() {
        auth.signOut()
    }


    fun getConversationsViaSnapshot(
        limit: Long,
        fromSnapshot: DocumentSnapshot?,
        onSuccess: ((lastDocumentSnapshot: DocumentSnapshot?, messages: List<Conversation>) -> Unit)?,
        onFailure: ((e: Exception) -> Unit)?
    ): ListenerRegistration {


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
        return query.addSnapshotListener(listener)
    }


    fun getMessagesViaSnapshot(
        limit: Long,
        upToSnapshot: DocumentSnapshot?,
        otherUserId: String,
        onSuccess: ((firstDocumentSnapshot: DocumentSnapshot?, messages: List<Message>) -> Unit)?,
        onFailure: ((e: Exception) -> Unit)?
    ): ListenerRegistration {

        val listener = object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    onFailure?.invoke(error)
                    return
                }

                var firstDocumentSnapshot: DocumentSnapshot? = null
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
                        if (firstDocumentSnapshot == null) firstDocumentSnapshot =
                            doc
                    } catch (e: Exception) {

                    }
                }
                onSuccess?.invoke(firstDocumentSnapshot, messages)

            }

        }

        var query = firestore.collection(messagesColumn.name)
            .whereIn(
                messagesColumn.senderReceiver,
                listOf(listOf(userId, otherUserId), listOf(otherUserId, userId))
            )
            .orderBy(messagesColumn.date, Query.Direction.ASCENDING)
            .limitToLast(limit)
        query = if (upToSnapshot == null) query else query.endBefore(upToSnapshot)
        return query.addSnapshotListener(listener)

    }


    fun getUsersViaSnapshot(
        startsWith: String,
        limit: Long,
        fromSnapshot: DocumentSnapshot?,
        onSuccess: ((lastDocumentSnapshot: DocumentSnapshot?, users: List<User>) -> Unit)?,
        onFailure: ((e: Exception) -> Unit)?
    ): ListenerRegistration {

        val listener = object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    onFailure?.invoke(error)
                    return
                }

                val documents = value!!.documents
                val users = mutableListOf<User>()
                var lastDocumentSnapshot: DocumentSnapshot? = null
                documents.forEach { doc ->
                    try {
                        users.add(User(doc.id, doc.getString(usersColumn.nameField)!!))
                        lastDocumentSnapshot = doc
                    } catch (e: Exception) {
                    }
                }

                onSuccess?.invoke(lastDocumentSnapshot, users.filter { it.id != userId })

            }

        }
        var query = firestore.collection(usersColumn.name)
            .limit(limit)
            .orderBy(usersColumn.nameField)
            .startAt(startsWith)
            .endAt("$startsWith\uf8ff")

        query = if (fromSnapshot == null) query else query.startAfter(fromSnapshot)
        return query.addSnapshotListener(listener)
    }


    suspend fun sendMessage(
        targetId: String,
        message: String,
        onSuccess: (() -> Unit)?,
        onFailure: ((e: Exception) -> Unit)?
    ) {


        try {
            /**
             * Date to be fixed by using server timestamp
             * later
             */
            val messageData = mapOf(
                messagesColumn.date to Date(),
                messagesColumn.message to message,
                messagesColumn.senderReceiver to listOf(userId, targetId)
            )

            firestore.collection(messagesColumn.name).add(messageData).await()

            val conversationCol = firestore.collection(conversationsColumn.name)
            val conversationQuery = { whichId: String ->
                conversationCol.whereEqualTo(conversationsColumn.owner, whichId)
                    .whereIn(
                        conversationsColumn.contacts,
                        listOf(listOf(userId, targetId), listOf(targetId, userId))
                    )
                    .get()
            }


            val myConversationSnapshot = conversationQuery(userId).await()
            val otherUserConversationSnapshot = conversationQuery(targetId).await()

            val newConversationData = { whichId: String ->
                /**
                 * Date to be fixed by using server timestamp
                 * later
                 */
                mapOf(
                    conversationsColumn.owner to whichId,
                    conversationsColumn.date to Date(),
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


    fun getUserViaSnapshotWithUserId(
        userId: String,
        onSuccess: ((user: User?) -> Unit)?,
        onFailure: ((e: Exception) -> Unit)?
    ): ListenerRegistration {

        val listener = object : EventListener<DocumentSnapshot> {
            override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    onFailure?.invoke(error)
                    return
                }
                var user: User? = null
                if (value != null) user = User(value.id, value.getString(usersColumn.nameField)!!)

                onSuccess?.invoke(user)
            }

        }

        return firestore.collection(usersColumn.name).document(userId).addSnapshotListener(listener)
    }


    suspend fun updateOwnAccountName(
        name: String,
        onSuccess: (() -> Unit)?,
        onFailure: ((e: Exception) -> Unit)?
    ) {
        try {
            firestore.collection(usersColumn.name).document(userId)
                .update(mapOf(usersColumn.nameField to name)).await()
            onSuccess?.invoke()
        } catch (e: Exception) {
            onFailure?.invoke(e)
        }
    }


}
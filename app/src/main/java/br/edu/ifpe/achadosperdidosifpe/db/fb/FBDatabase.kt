package br.edu.ifpe.achadosperdidosifpe.db.fb

import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class FBDatabase {
    interface Listener {
        fun onUserLoaded(user: FBUser)
        fun onUserSignOut()
        fun onItemAdded(item: FBItem)
        fun onItemUpdated(item: FBItem)
        fun onItemRemoved(item: FBItem)
    }

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private var itemsListReg: ListenerRegistration? = null
    private var listener: Listener? = null

    init {
        auth.addAuthStateListener { authState ->
            if (authState.currentUser == null) {
                itemsListReg?.remove()
                itemsListReg = null
                listener?.onUserSignOut()
                return@addAuthStateListener
            }
            val uid = authState.currentUser!!.uid
            db.collection("users").document(uid).get().addOnSuccessListener { document ->
                document.toObject(FBUser::class.java)?.let { user ->
                    listener?.onUserLoaded(user)
                }
            }
            setupItemsListener()
        }
    }

    fun setListener(listener: Listener? = null) {
        this.listener = listener
        if (listener != null) {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val uid = currentUser.uid
                db.collection("users").document(uid).get().addOnSuccessListener { document ->
                    document.toObject(FBUser::class.java)?.let { user ->
                        listener.onUserLoaded(user)
                    }
                }
                setupItemsListener()
            }
        } else {
            itemsListReg?.remove()
            itemsListReg = null
        }
    }

    private fun setupItemsListener() {
        itemsListReg?.remove()
        if (listener == null || auth.currentUser == null) return
        itemsListReg = db.collection("items")
            .addSnapshotListener { snapshots, ex ->
                if (ex != null) return@addSnapshotListener
                snapshots?.documentChanges?.forEach { change ->
                    val fbItem = change.document.toObject(FBItem::class.java)
                    when (change.type) {
                        DocumentChange.Type.ADDED -> listener?.onItemAdded(fbItem)
                        DocumentChange.Type.MODIFIED -> listener?.onItemUpdated(fbItem)
                        DocumentChange.Type.REMOVED -> listener?.onItemRemoved(fbItem)
                    }
                }
            }
    }

    fun register(user: FBUser): Task<Void> {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        val uid = auth.currentUser!!.uid
        return db.collection("users").document(uid).set(user)
    }

    fun getUser(uid: String, onComplete: (FBUser?) -> Unit) {
        if (uid.isBlank()) {
            onComplete(null)
            return
        }
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                onComplete(doc.toObject(FBUser::class.java))
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    fun add(item: FBItem) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        if (item.id.isEmpty())
            throw RuntimeException("Item with empty ID!")
        db.collection("items").document(item.id).set(item)
    }

    fun remove(item: FBItem) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        if (item.id.isEmpty())
            throw RuntimeException("Item with empty ID!")
        db.collection("items").document(item.id).delete()
    }

    fun createOrGetChat(
        itemId: String,
        ownerId: String,
        onComplete: (chatId: String) -> Unit
    ) {
        val currentUid = auth.currentUser?.uid ?: run {
            android.util.Log.e("CHAT_DEBUG", "usuário não logado")
            return
        }
        android.util.Log.d("CHAT_DEBUG", "currentUid: $currentUid, ownerId: $ownerId")
        val participants = listOf(currentUid, ownerId).sorted()
        val chatId = "${itemId}_${participants[0]}_${participants[1]}"
        val chatRef = db.collection("chats").document(chatId)
        chatRef.get().addOnSuccessListener { doc ->
            if (!doc.exists()) {
                val chat = FBChat(
                    id = chatId,
                    itemId = itemId,
                    participants = participants
                )
                chatRef.set(chat).addOnSuccessListener { onComplete(chatId) }
            } else {
                onComplete(chatId)
            }
        }
    }

    fun sendMessage(chatId: String, text: String) {
        val currentUid = auth.currentUser?.uid ?: return
        val messageId = db.collection("chats").document(chatId)
            .collection("messages").document().id
        val message = FBMessage(
            id = messageId,
            senderId = currentUid,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        val chatRef = db.collection("chats").document(chatId)
        val messageRef = chatRef.collection("messages").document(messageId)
        messageRef.set(message)
        chatRef.update(
            mapOf(
                "lastMessage" to text,
                "lastTimestamp" to message.timestamp
            )
        )
    }

    fun listenToMessages(
        chatId: String,
        onUpdate: (List<FBMessage>) -> Unit
    ): ListenerRegistration {
        return db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                val messages = snapshot?.documents
                    ?.mapNotNull { it.toObject(FBMessage::class.java) }
                    ?: emptyList()
                onUpdate(messages)
            }
    }

    fun listenToUserChats(
        onUpdate: (List<FBChat>) -> Unit
    ): ListenerRegistration {
        val currentUid = auth.currentUser?.uid ?: run {
            android.util.Log.e("CHAT_DEBUG", "listenToUserChats: usuário não logado")
            return db.collection("chats").addSnapshotListener { _, _ -> }
        }
        android.util.Log.d("CHAT_DEBUG", "listenToUserChats para uid: $currentUid")
        return db.collection("chats")
            .whereArrayContains("participants", currentUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("CHAT_DEBUG", "erro no listener: ${error.message}")
                    return@addSnapshotListener
                }
                val chats = snapshot?.documents
                    ?.mapNotNull { it.toObject(FBChat::class.java) }
                    ?.sortedByDescending { it.lastTimestamp }
                    ?: emptyList()
                android.util.Log.d("CHAT_DEBUG", "chats encontrados: ${chats.size}")
                onUpdate(chats)
            }
    }
}
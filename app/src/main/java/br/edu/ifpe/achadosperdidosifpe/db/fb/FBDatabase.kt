package br.edu.ifpe.achadosperdidosifpe.db.fb

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

    fun register(user: FBUser) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).set(user)
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
}
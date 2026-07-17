package br.edu.ifpe.achadosperdidosifpe.model

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import br.edu.ifpe.achadosperdidosifpe.db.fb.FBDatabase
import br.edu.ifpe.achadosperdidosifpe.db.fb.FBItem
import br.edu.ifpe.achadosperdidosifpe.db.fb.FBUser
import br.edu.ifpe.achadosperdidosifpe.db.fb.toFBItem
import br.edu.ifpe.achadosperdidosifpe.db.fb.toItem
import br.edu.ifpe.achadosperdidosifpe.db.fb.toUser
import android.net.Uri
import com.google.firebase.storage.storage
import java.io.File

class MainViewModel(
    private val db: FBDatabase
) : ViewModel(), FBDatabase.Listener {

    private val _items = mutableStateMapOf<String, Item>()

    val items: List<Item>
        get() = _items.values.toList().sortedByDescending { it.data }

    private val _user = mutableStateOf<User?>(null)
    val user: User?
        get() = _user.value


    private var _selectedItemId = mutableStateOf<String?>(null)
    var selectedItemId: String?
        get() = _selectedItemId.value
        set(value) { _selectedItemId.value = value }

    val selectedItem: Item?
        get() = _items[selectedItemId]

    init {
        db.setListener(this)
    }


    fun addItem(item: Item) {
        db.add(item.toFBItem())
    }

    fun removeItem(item: Item) {
        db.remove(item.toFBItem())
    }


    override fun onUserLoaded(user: FBUser) {
        _user.value = user.toUser()
    }

    override fun onUserSignOut() {
        _user.value = null
        _items.clear()
    }

    override fun onItemAdded(item: FBItem) {
        _items[item.id] = item.toItem()
    }

    override fun onItemUpdated(item: FBItem) {
        _items.remove(item.id)
        _items[item.id] = item.toItem()
    }

    override fun onItemRemoved(item: FBItem) {
        _items.remove(item.id)
    }

    fun addItemComFoto(item: Item, localPath: String?, onComplete: (Boolean) -> Unit) {
        if (localPath.isNullOrEmpty()) {

            db.add(item.toFBItem())
            onComplete(true)
            return
        }

        val fileUri = if (localPath.startsWith("/")) {
            Uri.fromFile(File(localPath))
        } else {
            Uri.parse(localPath)
        }

        val storageRef = com.google.firebase.Firebase.storage.reference
            .child("fotos_itens/${item.id}.jpg")

        storageRef.putFile(fileUri)
            .addOnSuccessListener {

                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val itemComUrlRemota = item.copy(fotoUrl = downloadUri.toString())
                    db.add(itemComUrlRemota.toFBItem())
                    onComplete(true)
                }.addOnFailureListener {
                    onComplete(false)
                }
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
}
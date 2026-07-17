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
}
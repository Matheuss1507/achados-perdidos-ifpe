package br.edu.ifpe.achadosperdidosifpe.model

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import br.edu.ifpe.achadosperdidosifpe.db.fb.FBChat
import br.edu.ifpe.achadosperdidosifpe.db.fb.FBDatabase
import br.edu.ifpe.achadosperdidosifpe.db.fb.FBItem
import br.edu.ifpe.achadosperdidosifpe.db.fb.FBMessage
import br.edu.ifpe.achadosperdidosifpe.db.fb.FBUser
import br.edu.ifpe.achadosperdidosifpe.db.fb.toFBItem
import br.edu.ifpe.achadosperdidosifpe.db.fb.toItem
import br.edu.ifpe.achadosperdidosifpe.db.fb.toUser
import br.edu.ifpe.achadosperdidosifpe.db.fb.toFBUser
import br.edu.ifpe.achadosperdidosifpe.notification.AppNotificationHelper
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID
import com.google.firebase.firestore.ListenerRegistration

class MainViewModel(
    private val db: FBDatabase
) : ViewModel(), FBDatabase.Listener {

    private val _items = mutableStateMapOf<String, Item>()
    val items: List<Item>
        get() = _items.values.toList().sortedByDescending { it.data }

    private val _user = mutableStateOf<User?>(null)
    val user: User?
        get() = _user.value

    private val _notifications = mutableStateListOf<AppNotification>()
    val notifications: List<AppNotification>
        get() = _notifications.sortedByDescending { it.timestamp }

    val unreadNotificationCount: Int
        get() = _notifications.count { !it.isRead }

    private var appContext: Context? = null

    fun setContext(context: Context) {
        this.appContext = context.applicationContext
    }

    init {
        db.setListener(this)
    }

    fun addNotification(notification: AppNotification) {
        _notifications.add(0, notification)
        appContext?.let { ctx ->
            AppNotificationHelper.showNotification(
                context = ctx,
                title = notification.title,
                message = notification.message,
                itemId = notification.itemId
            )
        }
    }

    fun markNotificationAsRead(id: String) {
        val index = _notifications.indexOfFirst { it.id == id }
        if (index != -1) {
            _notifications[index] = _notifications[index].copy(isRead = true)
        }
    }

    fun markAllNotificationsAsRead() {
        for (i in _notifications.indices) {
            _notifications[i] = _notifications[i].copy(isRead = true)
        }
    }

    fun addItem(item: Item) {
        db.add(item.toFBItem())
    }

    fun removeItem(item: Item) {
        db.remove(item.toFBItem())
    }

    fun updateUserProfile(updatedUser: User) {
        db.register(updatedUser.toFBUser())
        _user.value = updatedUser
    }

    override fun onUserLoaded(user: FBUser) {
        _user.value = user.toUser()
    }

    override fun onUserSignOut() {
        _user.value = null
        _items.clear()
        _notifications.clear()
    }

    override fun onItemAdded(item: FBItem) {
        val novoItem = item.toItem()
        _items[novoItem.id] = novoItem
        appContext?.let { ctx ->
            MatchEngine.processarNovoItem(
                context = ctx,
                novoItem = novoItem,
                listaGeralItens = items,
                usuarioAtualId = user?.id
            )
        }
    }

    override fun onItemUpdated(item: FBItem) {
        val itemAntigo = _items[item.id]
        val novoItem = item.toItem()

        if (itemAntigo != null && itemAntigo.status != novoItem.status) {
            if (novoItem.status == Status.RESOLVIDO || novoItem.status == Status.NO_SETOR) {
                val statusNome = if (novoItem.status == Status.RESOLVIDO) "RESOLVIDO" else "entregue no setor oficial"
                val notif = AppNotification(
                    id = UUID.randomUUID().toString(),
                    userId = novoItem.usuarioId,
                    title = "Status do Item Atualizado!",
                    message = "O status do seu item '${novoItem.nome}' mudou para: $statusNome.",
                    itemId = novoItem.id,
                    type = NotificationType.STATUS_CHANGED
                )
                addNotification(notif)
            }
        }

        _items.remove(item.id)
        _items[item.id] = novoItem
    }

    override fun onItemRemoved(item: FBItem) {
        _items.remove(item.id)
    }

    fun addItemComFoto(
        context: Context,
        item: Item,
        localPath: String?,
        onComplete: (Boolean) -> Unit
    ) {
        setContext(context)
        if (localPath.isNullOrEmpty()) {
            db.add(item.toFBItem())
            onComplete(true)
            return
        }
        try {
            val bytes = if (localPath.startsWith("content://") || localPath.startsWith("file://")) {
                val uri = android.net.Uri.parse(localPath)
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            } else {
                val file = File(localPath)
                if (!file.exists()) {
                    onComplete(false)
                    return
                }
                file.readBytes()
            }
            if (bytes == null) {
                onComplete(false)
                return
            }
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            val out = ByteArrayOutputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 50, out)
            val compressedBytes = out.toByteArray()
            val base64String = Base64.encodeToString(compressedBytes, Base64.NO_WRAP)
            val dataUri = "data:image/jpeg;base64,$base64String"
            val itemComFotoBase64 = item.copy(fotoUrl = dataUri)
            db.add(itemComFotoBase64.toFBItem())
            onComplete(true)
        } catch (e: Exception) {
            android.util.Log.e("ErroBase64", "Falha ao converter ou salvar imagem", e)
            onComplete(false)
        }
    }
    fun responderPerguntaVerificacao(item: Item, resposta: String, onComplete: (Boolean) -> Unit = {}) {
        val itemAtualizado = item.copy(respostaVerificacao = resposta)
        try {
            db.add(itemAtualizado.toFBItem())

            val notif = AppNotification(
                id = UUID.randomUUID().toString(),
                userId = item.usuarioId,
                title = "Nova Resposta de Verificação",
                message = "Um solicitante respondeu à pergunta de verificação do item '${item.nome}': \"$resposta\"",
                itemId = item.id,
                type = NotificationType.VERIFICATION_ANSWER
            )
            addNotification(notif)

            onComplete(true)
        } catch (e: Exception) {
            onComplete(false)
        }
    }

    fun confirmarDevolucao(item: Item, onComplete: (Boolean) -> Unit = {}) {
        val itemAtualizado = item.copy(status = Status.RESOLVIDO)
        try {
            db.add(itemAtualizado.toFBItem())
            onComplete(true)
        } catch (e: Exception) {
            onComplete(false)
        }
    }

    private val _chats = mutableStateListOf<FBChat>()
    val chats: List<FBChat> get() = _chats

    private val _messages = mutableStateListOf<FBMessage>()
    val messages: List<FBMessage> get() = _messages

    private var chatsListReg: ListenerRegistration? = null
    private var messagesListReg: ListenerRegistration? = null

    fun startListeningChats() {
        if (chatsListReg != null) return
        android.util.Log.d("CHAT_DEBUG", "já existe listener, ignorando")

        chatsListReg = db.listenToUserChats { list ->
            android.util.Log.d("CHAT_DEBUG", "chats recebidos: ${list.size}")

            _chats.clear()
            _chats.addAll(list)
        }
    }

    fun startListeningMessages(chatId: String) {
        messagesListReg?.remove()
        messagesListReg = db.listenToMessages(chatId) { list ->
            _messages.clear()
            _messages.addAll(list)
        }
    }

    fun stopListeningMessages() {
        messagesListReg?.remove()
        messagesListReg = null
        _messages.clear()
    }

    fun sendMessage(chatId: String, text: String) {
        db.sendMessage(chatId, text)
    }

    fun openOrCreateChat(
        itemId: String,
        ownerId: String,
        onReady: (chatId: String) -> Unit
    ) {
        db.createOrGetChat(itemId, ownerId, onReady)
    }

    override fun onCleared() {
        super.onCleared()
        chatsListReg?.remove()
        messagesListReg?.remove()
    }

    private val _chatInicialId = mutableStateOf<String?>(null)
    val chatInicialId: String? get() = _chatInicialId.value

    fun setChatInicial(chatId: String) {
        _chatInicialId.value = chatId
    }

    fun consumirChatInicial(): String? {
        val id = _chatInicialId.value
        _chatInicialId.value = null
        return id
    }
}
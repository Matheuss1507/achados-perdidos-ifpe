package br.edu.ifpe.achadosperdidosifpe.db.fb

import br.edu.ifpe.achadosperdidosifpe.model.Chat
import br.edu.ifpe.achadosperdidosifpe.model.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class FBMessage(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L
)

data class FBChat(
    val id: String = "",
    val itemId: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastTimestamp: Long = 0L
)

fun FBChat.toChat(nomeOutro: String, papelOutro: String, nomeItem: String): Chat {
    val horario = if (lastTimestamp > 0L)
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(lastTimestamp))
    else ""
    return Chat(
        id = id,
        nome = nomeOutro,
        papel = papelOutro,
        tituloItem = nomeItem,
        ultimaMensagem = lastMessage,
        horario = horario
    )
}

fun FBMessage.toMessage(currentUserId: String): Message {
    val horario = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
    return Message(
        texto = text,
        horario = horario,
        enviadoPorMim = senderId == currentUserId
    )
}
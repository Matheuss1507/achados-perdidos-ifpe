package br.edu.ifpe.achadosperdidosifpe.model

import java.util.Date

enum class NotificationType {
    MATCH,
    VERIFICATION_ANSWER,
    STATUS_CHANGED
}

data class AppNotification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val itemId: String = "",
    val isRead: Boolean = false,
    val timestamp: Date = Date(),
    val type: NotificationType = NotificationType.MATCH
)
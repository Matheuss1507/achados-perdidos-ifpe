package br.edu.ifpe.achadosperdidosifpe.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import br.edu.ifpe.achadosperdidosifpe.MainActivity
import br.edu.ifpe.achadosperdidosifpe.R
import br.edu.ifpe.achadosperdidosifpe.model.Item

object MatchNotificationHelper {

    private const val CHANNEL_ID = "MATCH_NOTIFICATIONS_CHANNEL"
    private const val CHANNEL_NAME = "Notificações de Match"
    private const val CHANNEL_DESC = "Notifica usuários sobre itens perdidos encontrados"

    fun showMatchNotification(context: Context, itemEncontrado: Item) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_SCREEN", "details")
            putExtra("ITEM_ID", itemEncontrado.id)
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            itemEncontrado.id.hashCode(),
            intent,
            pendingIntentFlags
        )

        val setorNome = itemEncontrado.setor ?: "no campus"
        val titulo = "Item Similar Encontrado!"
        val mensagem = "Um item similar ao seu foi encontrado no $setorNome!"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.achados_perdidos_logo)
            .setContentTitle(titulo)
            .setContentText(mensagem)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensagem))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(itemEncontrado.id.hashCode(), builder.build())
    }
}
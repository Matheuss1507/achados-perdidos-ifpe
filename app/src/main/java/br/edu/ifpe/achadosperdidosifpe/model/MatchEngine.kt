package br.edu.ifpe.achadosperdidosifpe.model

import android.content.Context
import br.edu.ifpe.achadosperdidosifpe.notification.MatchNotificationHelper
import java.util.Calendar

object MatchEngine {
    fun processarNovoItem(
        context: Context,
        novoItem: Item,
        listaGeralItens: List<Item>,
        usuarioAtualId: String?
    ) {
        if (novoItem.tipo != Tipo.ENCONTRADO) return

        val limiteDiasAtras = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -30)
        }.time

        val itensCorrespondentes = listaGeralItens.filter { itemExistente ->
            val ehPerdido = itemExistente.tipo == Tipo.PERDIDO
            val naoEstaResolvido = itemExistente.status != Status.RESOLVIDO

            val ehDoUsuarioDono = usuarioAtualId == null || itemExistente.usuarioId == usuarioAtualId

            val mesmaCategoria = itemExistente.categoria.equals(novoItem.categoria, ignoreCase = true)
            val mesmoSetor = !novoItem.setor.isNullOrBlank() &&
                    itemExistente.setor.equals(novoItem.setor, ignoreCase = true)

            val ehRecente = itemExistente.data != null && itemExistente.data.after(limiteDiasAtras)

            ehPerdido && naoEstaResolvido && ehDoUsuarioDono && mesmaCategoria && mesmoSetor && ehRecente
        }

        if (itensCorrespondentes.isNotEmpty()) {
            MatchNotificationHelper.showMatchNotification(context, novoItem)
        }
    }
}
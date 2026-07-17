package br.edu.ifpe.achadosperdidosifpe.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

enum class Tipo {
    PERDIDO,
    ENCONTRADO
}

enum class Status {
    PERDIDO,
    NO_SETOR,
    RESOLVIDO
}

enum class MetodoDevolucao {
    LEVAR_AO_SETOR,
    DEVOLVER_PESSOALMENTE
}

data class Item(
    val id: String = "",
    val usuarioId: String = "",
    val tipo: Tipo = Tipo.PERDIDO,
    val status: Status = Status.PERDIDO,
    val nome: String = "",
    val categoria: String = "",
    val corPrincipal: String? = null,
    val localizacao: String = "",
    val caracteristicasUnicas: String? = null,
    val descricao: String? = null,
    val metodoDevolucao: MetodoDevolucao? = null,
    val perguntaVerificacao: String? = null,


    val fotoUrl: String? = null,

    @ServerTimestamp
    val data: Date? = null
)
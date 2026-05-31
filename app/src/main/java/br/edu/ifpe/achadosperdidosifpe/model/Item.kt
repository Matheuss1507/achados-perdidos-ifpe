package br.edu.ifpe.achadosperdidosifpe.model

import java.util.Date
enum class Tipo {
    PERDIDO,
    ENCONTRADO
}

enum class Status{
    PERDIDO,
    NO_SETOR,
    RESOLVIDO
}

enum class MetodoDevolucao {
    LEVAR_AO_SETOR,
    DEVOLVER_PESSOALMENTE
}

data class Item(
    val id: String,
    val usuarioId: String,
    val tipo: Tipo,
    val status: Status,
    val nome: String,
    val categoria: String,
    val corPrincipal: String? = null,
    val localizacao: String,
    val caracteristicasUnicas: String? = null,
    val descricao: String? = null,
    val metodoDevolucao: MetodoDevolucao? = null,
    val perguntaVerificacao: String? = null,
    val fotoMockResId: Int? = null,
    val data: Date
)

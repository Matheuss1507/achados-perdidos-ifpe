package br.edu.ifpe.achadosperdidosifpe.model

data class Message(
    val texto: String,
    val horario: String,
    val enviadoPorMim: Boolean
)
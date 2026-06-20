package br.edu.ifpe.achadosperdidosifpe.model

data class Chat(
    val id: String,
    val nome: String,
    val papel: String,
    val tituloItem: String,
    val ultimaMensagem: String,
    val horario: String
)

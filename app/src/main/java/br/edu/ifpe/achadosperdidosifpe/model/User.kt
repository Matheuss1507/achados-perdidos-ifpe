package br.edu.ifpe.achadosperdidosifpe.model

data class User(
    val id: String,
    val nome: String,
    val email: String,
    val matricula: String?,
    val curso: String,
    val tipo: String
)

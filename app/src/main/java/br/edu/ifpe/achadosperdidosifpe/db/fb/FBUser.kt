package br.edu.ifpe.achadosperdidosifpe.db.fb

import br.edu.ifpe.achadosperdidosifpe.model.User

data class FBUser(
    val id: String = "",
    val nome: String = "",
    val email: String = "",
    val matricula: String? = null,
    val curso: String = "",
    val tipo: String = ""
)


fun FBUser.toUser(): User {
    return User(
        id = this.id,
        nome = this.nome,
        email = this.email,
        matricula = this.matricula,
        curso = this.curso,
        tipo = this.tipo
    )
}

fun User.toFBUser(): FBUser {

    return FBUser(
        id = this.id,
        nome = this.nome,
        email = this.email,
        matricula = this.matricula,
        curso = this.curso,
        tipo = this.tipo
    )
}
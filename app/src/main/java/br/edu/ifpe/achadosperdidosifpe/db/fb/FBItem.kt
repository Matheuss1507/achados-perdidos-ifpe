package br.edu.ifpe.achadosperdidosifpe.db.fb


import br.edu.ifpe.achadosperdidosifpe.model.Item
import br.edu.ifpe.achadosperdidosifpe.model.MetodoDevolucao
import br.edu.ifpe.achadosperdidosifpe.model.Status
import br.edu.ifpe.achadosperdidosifpe.model.Tipo
import java.util.Date

data class FBItem(
    val id: String = "",
    val usuarioId: String = "",
    val tipo: String = "",
    val status: String = "",
    val nome: String = "",
    val categoria: String = "",
    val corPrincipal: String? = null,
    val localizacao: String = "",
    val caracteristicasUnicas: String? = null,
    val descricao: String? = null,
    val metodoDevolucao: String? = null,
    val perguntaVerificacao: String? = null,
    val fotoUrl: String? = null,
    val data: Date = Date()
)

fun FBItem.toItem(): Item {
    return Item(
        id = this.id,
        usuarioId = this.usuarioId,
        tipo = runCatching { Tipo.valueOf(this.tipo) }.getOrDefault(Tipo.PERDIDO),
        status = runCatching { Status.valueOf(this.status) }.getOrDefault(Status.PERDIDO),
        nome = this.nome,
        categoria = this.categoria,
        corPrincipal = this.corPrincipal,
        localizacao = this.localizacao,
        caracteristicasUnicas = this.caracteristicasUnicas,
        descricao = this.descricao,
        metodoDevolucao = this.metodoDevolucao?.let {
            runCatching { MetodoDevolucao.valueOf(it) }.getOrNull()
        },
        perguntaVerificacao = this.perguntaVerificacao,
        fotoUrl = this.fotoUrl,
        data = this.data
    )
}

fun Item.toFBItem(): FBItem {
    return FBItem(
        id = this.id,
        usuarioId = this.usuarioId,
        tipo = this.tipo.name,
        status = this.status.name,
        nome = this.nome,
        categoria = this.categoria,
        corPrincipal = this.corPrincipal,
        localizacao = this.localizacao,
        caracteristicasUnicas = this.caracteristicasUnicas,
        descricao = this.descricao,
        metodoDevolucao = this.metodoDevolucao?.name,
        perguntaVerificacao = this.perguntaVerificacao,
        fotoUrl = this.fotoUrl,
        data = this.data
    )
}
package br.edu.ifpe.achadosperdidosifpe.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.achadosperdidosifpe.model.MainViewModel
import br.edu.ifpe.achadosperdidosifpe.model.Status
import br.edu.ifpe.achadosperdidosifpe.model.Tipo
import br.edu.ifpe.achadosperdidosifpe.model.User
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onLogoutClick: () -> Unit = {}
) {
    val ifpeGreen = Color(0xFF00642F)
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val user = viewModel.user

    val meusItens = viewModel.items.filter { it.usuarioId == user?.id }
    val qtdPerdidos = meusItens.count { it.tipo == Tipo.PERDIDO }
    val qtdEncontrados = meusItens.count { it.tipo == Tipo.ENCONTRADO }
    val qtdDevolucoes = meusItens.count { it.status == Status.RESOLVIDO }

    var showEditProfileDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .verticalScroll(scrollState)
    ) {
        // --- HEADER PERFIL ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ifpeGreen)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text("Meu Perfil", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = Color.White, modifier = Modifier.size(76.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil", tint = ifpeGreen, modifier = Modifier.size(44.dp))
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(user?.nome ?: "Carregando...", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(user?.curso ?: "", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Cards de Métricas do Usuário
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard(titulo = "Itens\nPerdidos", valor = "$qtdPerdidos", cor = Color(0xFFD32F2F), modifier = Modifier.weight(1f))
                MetricCard(titulo = "Itens\nEncontrados", valor = "$qtdEncontrados", cor = Color(0xFF388E3C), modifier = Modifier.weight(1f))
                MetricCard(titulo = "Devoluções\n", valor = "$qtdDevolucoes", cor = Color(0xFF1976D2), modifier = Modifier.weight(1f))
            }

            // --- INFORMAÇÕES PESSOAIS ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Informações Pessoais", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    IconButton(onClick = { if (user != null) showEditProfileDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar Perfil", tint = ifpeGreen)
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFEFEFEF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        InfoRow(icon = Icons.Default.Email, label = "E-mail", valor = user?.email ?: "-")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF5F5F5))
                        InfoRow(icon = Icons.Default.Assignment, label = "Matrícula", valor = user?.matricula ?: "Não informada")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF5F5F5))
                        InfoRow(icon = Icons.Default.School, label = "Curso / Setor", valor = user?.curso ?: "-")
                    }
                }
            }

            // Botão Editar Perfil
            OutlinedButton(
                onClick = { if (user != null) showEditProfileDialog = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ifpeGreen)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = ifpeGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar Perfil", color = ifpeGreen, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            // Botão Sair da Conta
            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text("Sair da Conta", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Diálogo de Edição de Perfil
    if (showEditProfileDialog && user != null) {
        EditProfileDialog(
            user = user,
            onDismiss = { showEditProfileDialog = false },
            onSave = { updatedUser ->
                viewModel.updateUserProfile(updatedUser)
                Toast.makeText(context, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                showEditProfileDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (User) -> Unit
) {
    var nome by remember { mutableStateOf(user.nome) }
    var matricula by remember { mutableStateOf(user.matricula ?: "") }
    var cursoExpanded by remember { mutableStateOf(false) }
    var cursoSelecionado by remember { mutableStateOf(user.curso) }

    val cursos = listOf(
        "Análise e Desenvolvimento de Sistemas",
        "Engenharia de Computação",
        "Redes de Computadores",
        "Administração",
        "Contabilidade",
        "Outro"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = matricula,
                    onValueChange = { matricula = it },
                    label = { Text("Matrícula") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                ExposedDropdownMenuBox(
                    expanded = cursoExpanded,
                    onExpandedChange = { cursoExpanded = it }
                ) {
                    OutlinedTextField(
                        value = cursoSelecionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Curso / Setor") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cursoExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = cursoExpanded,
                        onDismissRequest = { cursoExpanded = false }
                    ) {
                        cursos.forEach { curso ->
                            DropdownMenuItem(
                                text = { Text(curso) },
                                onClick = {
                                    cursoSelecionado = curso
                                    cursoExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nome.isNotBlank()) {
                        val userAtualizado = user.copy(
                            nome = nome,
                            matricula = matricula.ifBlank { null },
                            curso = cursoSelecionado
                        )
                        onSave(userAtualizado)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IfpeGreen)
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) }
        }
    )
}

@Composable
private fun MetricCard(titulo: String, valor: String, cor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEFEFEF))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valor, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = cor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(titulo, fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(valor, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        }
    }
}
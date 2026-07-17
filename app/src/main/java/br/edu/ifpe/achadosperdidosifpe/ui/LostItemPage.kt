package br.edu.ifpe.achadosperdidosifpe.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.achadosperdidosifpe.model.MainViewModel
import br.edu.ifpe.achadosperdidosifpe.model.Item
import br.edu.ifpe.achadosperdidosifpe.model.Tipo
import br.edu.ifpe.achadosperdidosifpe.model.Status
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

val IfpeGreen = Color(0xFF00642F)
val IfpeGreenMid = Color(0xFF00913F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostItemPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToItems: () -> Unit = {}
) {
    var nome by remember { mutableStateOf("") }
    var categoriaExpanded by remember { mutableStateOf(false) }
    var categoriaSelecionada by remember { mutableStateOf("") }
    var corPrincipal by remember { mutableStateOf("") }
    var localizacao by remember { mutableStateOf("") }
    var data by remember { mutableStateOf("") }
    var caracteristicas by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    val categorias = listOf(
        "Documentos",
        "Eletrônicos",
        "Acessórios",
        "Vestuário",
        "Material escolar",
        "Outros"
    )
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = IfpeGreen
                )
            }
            Text(
                text = "Perdi um item",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        HorizontalDivider(color = Color(0xFFEEEEEE))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FormField(label = "O que você perdeu?") {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    placeholder = { Text("Ex: Caderno de Cálculo", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true
                )
            }

            FormField(label = "Categoria") {
                ExposedDropdownMenuBox(
                    expanded = categoriaExpanded,
                    onExpandedChange = { categoriaExpanded = it }
                ) {
                    OutlinedTextField(
                        value = categoriaSelecionada,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Selecione...", color = Color.LightGray) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(10.dp),
                        colors = fieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = categoriaExpanded,
                        onDismissRequest = { categoriaExpanded = false }
                    ) {
                        categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria) },
                                onClick = {
                                    categoriaSelecionada = categoria
                                    categoriaExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            FormField(label = "Cor Principal", icon = Icons.Default.Palette) {
                OutlinedTextField(
                    value = corPrincipal,
                    onValueChange = { corPrincipal = it },
                    placeholder = { Text("Ex: Azul escuro", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true
                )
            }

            HorizontalDivider(color = Color(0xFFEEEEEE))

            FormField(label = "Onde perdeu?", icon = Icons.Default.LocationOn) {
                OutlinedTextField(
                    value = localizacao,
                    onValueChange = { localizacao = it },
                    placeholder = { Text("Selecionar no mapa...", color = Color.LightGray) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Abrir mapa",
                            tint = IfpeGreenMid,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true
                )
            }

            FormField(label = "Quando perdeu?", icon = Icons.Default.CalendarMonth) {
                OutlinedTextField(
                    value = data,
                    onValueChange = { data = it },
                    placeholder = { Text("dd/mm/aaaa", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true
                )
            }

            HorizontalDivider(color = Color(0xFFEEEEEE))

            FormField(label = "Características únicas", icon = Icons.Default.Fingerprint) {
                OutlinedTextField(
                    value = caracteristicas,
                    onValueChange = { caracteristicas = it },
                    placeholder = {
                        Text(
                            "Ex: Tem um adesivo do Python, página 47 marcada.",
                            color = Color.LightGray
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    maxLines = 4
                )
            }

            FormField(label = "Descrição Adicional") {
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    placeholder = { Text("Qualquer informação adicional...", color = Color.LightGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    maxLines = 3
                )
            }

            Button(
                onClick = {
                    if (nome.isBlank() || categoriaSelecionada.isBlank() || localizacao.isBlank() || data.isBlank()) {
                        Toast.makeText(context, "Por favor, preencha os campos obrigatórios (*)", Toast.LENGTH_SHORT).show()
                    } else {
                        val parsedDate = try {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(data) ?: Date()
                        } catch (e: Exception) {
                            Date()
                        }

                        val item = Item(
                            id = UUID.randomUUID().toString(),
                            usuarioId = viewModel.user?.id ?: "user_anonimo",
                            tipo = Tipo.PERDIDO,
                            status = Status.PERDIDO,
                            nome = nome,
                            categoria = categoriaSelecionada,
                            corPrincipal = corPrincipal.ifBlank { null },
                            localizacao = localizacao,
                            caracteristicasUnicas = caracteristicas.ifBlank { null },
                            descricao = descricao.ifBlank { null },
                            data = parsedDate
                        )

                        viewModel.addItem(item)
                        Toast.makeText(context, "Item perdido registrado!", Toast.LENGTH_SHORT).show()
                        onNavigateToItems()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IfpeGreen)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Registrar item perdido",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun FormField(
    label: String,
    icon: ImageVector? = null,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = IfpeGreenMid,
                    modifier = Modifier.size(15.dp)
                )
            }
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
        }
        content()
    }
}

@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = IfpeGreen,
    unfocusedBorderColor = Color(0xFFDDDDDD),
    focusedLabelColor = IfpeGreen,
    unfocusedContainerColor = Color.White,
    focusedContainerColor = Color.White
)
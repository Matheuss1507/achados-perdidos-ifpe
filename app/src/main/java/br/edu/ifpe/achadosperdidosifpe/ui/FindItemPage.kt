package br.edu.ifpe.achadosperdidosifpe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindItemPage(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
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
                text = "Encontrei um item",
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFFDE7), RoundedCornerShape(8.dp))
                    .border(0.5.dp, Color(0xFFF9A825), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFF57F17),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Dica de Segurança: Não inclua detalhes muito específicos na foto. " +
                            "Deixe alguns detalhes para verificação do dono.",
                    fontSize = 12.sp,
                    color = Color(0xFF5D4037),
                    lineHeight = 18.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = IfpeGreenMid,
                    modifier = Modifier.size(15.dp)
                )
                Text("Foto do Item (opcional)", fontSize = 13.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .border(1.5.dp, Color(0xFFBBBBBB), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(36.dp)
                    )
                    Text("Clique para adicionar foto", fontSize = 13.sp, color = Color.Gray)
                    Text("Sem detalhes muito específicos", fontSize = 11.sp, color = Color.LightGray)
                }
            }

            FormField(label = "O que você encontrou?") {
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

            FormField(label = "Onde encontrou?", icon = Icons.Default.LocationOn) {
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

            FormField(label = "Quando encontrou?", icon = Icons.Default.CalendarMonth) {
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

            FormField(label = "Características Únicas", icon = Icons.Default.Fingerprint) {
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
                onClick = { /* TODO: salvar item e navegar de volta */ },
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
                    fontWeight = FontWeight.Bold
                )
            }
        }
}}


@Composable
private fun FormField(
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
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = IfpeGreen,
    unfocusedBorderColor = Color(0xFFDDDDDD),
    focusedLabelColor = IfpeGreen,
    unfocusedContainerColor = Color.White,
    focusedContainerColor = Color.White
)
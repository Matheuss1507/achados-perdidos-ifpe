package br.edu.ifpe.achadosperdidosifpe.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.achadosperdidosifpe.model.Item
import br.edu.ifpe.achadosperdidosifpe.model.MainViewModel
import br.edu.ifpe.achadosperdidosifpe.model.Status
import br.edu.ifpe.achadosperdidosifpe.model.Tipo
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreen
import coil.compose.SubcomposeAsyncImage

private val GreenFilter = Color(0xFF00913F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onBackClick: () -> Unit = {},
    onItemClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val currentUserId = viewModel.user?.id

    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    var currentFilter by remember { mutableStateOf(ItemFilter()) }
    var showFilterSheet by remember { mutableStateOf(false) }

    var itemParaDeletar by remember { mutableStateOf<Item?>(null) }
    var itemParaEditar by remember { mutableStateOf<Item?>(null) }

    val scrollState = rememberScrollState()

    val itensFiltrados = viewModel.items.filter { item ->
        val matchesSearch = item.nome.contains(searchText, ignoreCase = true) ||
                item.categoria.contains(searchText, ignoreCase = true)
        val matchesTab = when (selectedTab) {
            1 -> item.tipo == Tipo.PERDIDO
            2 -> item.tipo == Tipo.ENCONTRADO
            3 -> item.usuarioId == currentUserId
            else -> true
        }
        val matchesCategoria = currentFilter.categoria == null || item.categoria.equals(currentFilter.categoria, ignoreCase = true)
        val matchesTipo = currentFilter.tipo == null || item.tipo == currentFilter.tipo
        val matchesStatus = currentFilter.status == null || item.status == currentFilter.status
        val matchesCor = currentFilter.cor == null || item.corPrincipal?.contains(currentFilter.cor!!, ignoreCase = true) == true

        matchesSearch && matchesTab && matchesCategoria && matchesTipo && matchesStatus && matchesCor
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // --- HEADER TIPO CATÁLOGO DE ITENS ---
        Surface(
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Explorar Itens",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "${itensFiltrados.size} item(ns) encontrado(s)",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    FilledTonalIconButton(
                        onClick = { showFilterSheet = true },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (currentFilter.isActive) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)
                        )
                    ) {
                        BadgedBox(
                            badge = {
                                if (currentFilter.isActive) {
                                    Badge(containerColor = GreenFilter)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filtrar Itens",
                                tint = if (currentFilter.isActive) GreenFilter else Color(0xFF475569)
                            )
                        }
                    }
                }

                // Campo de Busca do Catálogo
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = {
                            Text("Filtrar por nome ou categoria...", fontSize = 13.sp, color = Color(0xFF94A3B8))
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Buscar", tint = GreenFilter)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenFilter,
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Abas de Filtro
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = GreenFilter,
                    edgePadding = 16.dp,
                    indicator = { tabPositions: List<TabPosition> ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = GreenFilter,
                                height = 3.dp
                            )
                        }
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Todos", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium, fontSize = 14.sp) },
                        selectedContentColor = GreenFilter,
                        unselectedContentColor = Color(0xFF64748B)
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Perdidos", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium, fontSize = 14.sp) },
                        selectedContentColor = GreenFilter,
                        unselectedContentColor = Color(0xFF64748B)
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Encontrados", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium, fontSize = 14.sp) },
                        selectedContentColor = GreenFilter,
                        unselectedContentColor = Color(0xFF64748B)
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("Meus Itens", fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Medium, fontSize = 14.sp) },
                        selectedContentColor = GreenFilter,
                        unselectedContentColor = Color(0xFF64748B)
                    )
                }
            }
        }

        // --- LISTA DO CATÁLOGO ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (itensFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Nenhum item encontrado",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155),
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tente alterar os filtros ou a aba selecionada.",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                itensFiltrados.forEach { item ->
                    ItemsPageCard(
                        item = item,
                        showEditDelete = (selectedTab == 3),
                        onClick = { onItemClick(item.id) },
                        onEditClick = { itemParaEditar = item },
                        onDeleteClick = { itemParaDeletar = item }
                    )
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            currentFilter = currentFilter,
            onDismissRequest = { showFilterSheet = false },
            onApplyFilter = { newFilter -> currentFilter = newFilter }
        )
    }

    // Diálogo de Exclusão de Item
    itemParaDeletar?.let { item ->
        AlertDialog(
            onDismissRequest = { itemParaDeletar = null },
            title = { Text("Excluir Item") },
            text = { Text("Tem certeza que deseja excluir '${item.nome}'? Esta ação é irreversível.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeItem(item)
                        Toast.makeText(context, "Item removido com sucesso!", Toast.LENGTH_SHORT).show()
                        itemParaDeletar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) { Text("Excluir", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { itemParaDeletar = null }) { Text("Cancelar", color = Color.Gray) }
            }
        )
    }

    // Diálogo de Edição de Item
    itemParaEditar?.let { item ->
        EditItemDialog(
            item = item,
            onDismiss = { itemParaEditar = null },
            onSave = { itemEditado: Item ->
                viewModel.addItem(itemEditado)
                Toast.makeText(context, "Item atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                itemParaEditar = null
            }
        )
    }
}

@Composable
fun ItemsPageCard(
    item: Item,
    showEditDelete: Boolean,
    onClick: () -> Unit,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val imageModel = remember(item.fotoUrl) {
        if (item.fotoUrl?.startsWith("data:image") == true) {
            try {
                val base64String = item.fotoUrl.substringAfter(",")
                android.util.Base64.decode(base64String, android.util.Base64.DEFAULT)
            } catch (e: Exception) {
                item.fotoUrl
            }
        } else {
            item.fotoUrl
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(Color(0xFFF1F5F9), shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!item.fotoUrl.isNullOrEmpty()) {
                    SubcomposeAsyncImage(
                        model = imageModel,
                        contentDescription = "Foto de ${item.nome}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = IfpeGreen,
                                    strokeWidth = 2.dp
                                )
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BrokenImage,
                                    contentDescription = "Erro ao carregar imagem",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = "Sem imagem",
                        tint = Color.LightGray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isPerdido = item.tipo == Tipo.PERDIDO
                    val tagBgColor = if (isPerdido) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
                    val tagTextColor = if (isPerdido) Color(0xFF991B1B) else Color(0xFF166534)
                    val tagText = if (isPerdido) "PERDIDO" else "ENCONTRADO"

                    Surface(
                        color = tagBgColor,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = tagText,
                            color = tagTextColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = item.nome,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Localização",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.localizacao,
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }

            if (showEditDelete) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar Item", tint = IfpeGreen)
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir Item", tint = Color(0xFFD32F2F))
                    }
                }
            } else {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Ver detalhes",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemDialog(item: Item, onDismiss: () -> Unit, onSave: (Item) -> Unit) {
    var nome by remember { mutableStateOf(item.nome) }
    var localizacao by remember { mutableStateOf(item.localizacao) }
    var perguntaVerificacao by remember { mutableStateOf(item.perguntaVerificacao ?: "") }
    var statusSelecionado by remember { mutableStateOf(item.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Item", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = localizacao,
                    onValueChange = { localizacao = it },
                    label = { Text("Localização") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = perguntaVerificacao,
                    onValueChange = { perguntaVerificacao = it },
                    label = { Text("Pergunta de Verificação") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Status do Item", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Status.entries.forEach { status ->
                        FilterChip(
                            selected = statusSelecionado == status,
                            onClick = { statusSelecionado = status },
                            label = { Text(status.name, fontSize = 10.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val itemAtualizado = item.copy(
                        nome = nome,
                        localizacao = localizacao,
                        perguntaVerificacao = perguntaVerificacao.ifBlank { null },
                        status = statusSelecionado
                    )
                    onSave(itemAtualizado)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IfpeGreen)
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) }
        }
    )
}
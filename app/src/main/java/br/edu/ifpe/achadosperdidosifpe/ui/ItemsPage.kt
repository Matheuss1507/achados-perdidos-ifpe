package br.edu.ifpe.achadosperdidosifpe.ui

import android.util.Base64
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
import br.edu.ifpe.achadosperdidosifpe.ui.theme.fieldColors
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
        Surface(color = Color.White, shadowElevation = 2.dp) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Explorar Itens", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text("${itensFiltrados.size} item(ns) encontrado(s)", fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    FilledTonalIconButton(
                        onClick = { showFilterSheet = true },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (currentFilter.isActive) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)
                        )
                    ) {
                        BadgedBox(badge = { if (currentFilter.isActive) Badge(containerColor = GreenFilter) }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filtrar", tint = if (currentFilter.isActive) GreenFilter else Color(0xFF475569))
                        }
                    }
                }
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Filtrar por nome ou categoria...", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = GreenFilter) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
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
                    listOf("Todos", "Perdidos", "Encontrados", "Meus Itens").forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium, fontSize = 14.sp) },
                            selectedContentColor = GreenFilter,
                            unselectedContentColor = Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (itensFiltrados.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                    Text("Nenhum item encontrado", fontWeight = FontWeight.Bold, color = Color(0xFF64748B), fontSize = 15.sp)
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

    itemParaDeletar?.let { item ->
        AlertDialog(
            onDismissRequest = { itemParaDeletar = null },
            title = { Text("Excluir Item", fontWeight = FontWeight.Bold) },
            text = { Text("Tem certeza que deseja excluir '${item.nome}'? Esta ação é irreversível.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeItem(item)
                        Toast.makeText(context, "Item removido com sucesso!", Toast.LENGTH_SHORT).show()
                        itemParaDeletar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) { Text("Excluir", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { itemParaDeletar = null }) { Text("Cancelar", color = Color(0xFF64748B)) }
            }
        )
    }

    itemParaEditar?.let { item ->
        EditItemDialog(
            item = item,
            onDismiss = { itemParaEditar = null },
            onSave = { itemEditado ->
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
    // Decodifica a foto se ela for uma String Base64 (data:image/...)
    val imageModel = remember(item.fotoUrl) {
        if (item.fotoUrl?.startsWith("data:image") == true) {
            try {
                val base64String = item.fotoUrl.substringAfter(",")
                Base64.decode(base64String, Base64.DEFAULT)
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
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
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
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = IfpeGreen, strokeWidth = 2.dp)
                            }
                        },
                        error = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.BrokenImage, contentDescription = null, tint = Color.LightGray)
                            }
                        }
                    )
                } else {
                    Icon(Icons.Default.Inbox, contentDescription = null, tint = Color.LightGray)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(item.nome, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                Text(item.localizacaoFormatada, fontSize = 12.sp, color = Color(0xFF64748B), maxLines = 1)
            }
            if (showEditDelete) {
                Row {
                    IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, contentDescription = "Editar", tint = IfpeGreen) }
                    IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color(0xFFDC2626)) }
                }
            } else {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF94A3B8))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditItemDialog(item: Item, onDismiss: () -> Unit, onSave: (Item) -> Unit) {
    var nome by remember { mutableStateOf(item.nome) }
    var setor by remember { mutableStateOf(item.setor ?: "") } // <--- Novo estado
    var perguntaVerificacao by remember { mutableStateOf(item.perguntaVerificacao ?: "") }
    var statusSelecionado by remember { mutableStateOf(item.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("Editar Item", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0F172A))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do Item") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = setor,
                    onValueChange = { setor = it },
                    label = { Text("Setor / Bloco") }, // <--- Atualizado
                    placeholder = { Text("Ex: Bloco A, Biblioteca...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = perguntaVerificacao,
                    onValueChange = { perguntaVerificacao = it },
                    label = { Text("Pergunta de Verificação") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors()
                )
                Text("Status do Item", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Status.entries.forEach { status ->
                        val isSelected = statusSelecionado == status
                        FilterChip(
                            selected = isSelected,
                            onClick = { statusSelecionado = status },
                            label = { Text(status.name, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IfpeGreen,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF1F5F9),
                                labelColor = Color(0xFF334155)
                            )
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
                        setor = setor.ifBlank { null }, // <--- Salva o novo campo setor
                        perguntaVerificacao = perguntaVerificacao.ifBlank { null },
                        status = statusSelecionado
                    )
                    onSave(itemAtualizado)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IfpeGreen),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Salvar Alterações", color = Color.White, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = Color(0xFF64748B)) }
        }
    )
}
package br.edu.ifpe.achadosperdidosifpe.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.achadosperdidosifpe.model.Item
import br.edu.ifpe.achadosperdidosifpe.model.MainViewModel
import br.edu.ifpe.achadosperdidosifpe.model.Tipo
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreen

private val GreenFilter = Color(0xFF00913F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onBackClick: () -> Unit = {},
    onItemClick: (String) -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    var currentFilter by remember { mutableStateOf(ItemFilter()) }
    var showFilterSheet by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val itensFiltrados = viewModel.items.filter { item ->
        val matchesSearch = item.nome.contains(searchText, ignoreCase = true) ||
                item.categoria.contains(searchText, ignoreCase = true)
        val matchesTab = when (selectedTab) {
            1 -> item.tipo == Tipo.PERDIDO
            2 -> item.tipo == Tipo.ENCONTRADO
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
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = GreenFilter,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = GreenFilter,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "Todos",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        },
                        selectedContentColor = GreenFilter,
                        unselectedContentColor = Color(0xFF64748B)
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "Perdidos",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        },
                        selectedContentColor = GreenFilter,
                        unselectedContentColor = Color(0xFF64748B)
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                text = "Encontrados",
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        },
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
                            text = "Tente alterar os filtros ou o termo de busca.",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                itensFiltrados.forEach { item ->
                    ItemsPageCard(item = item, onClick = { onItemClick(item.id) })
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
}

@Composable
fun ItemsPageCard(item: Item, onClick: () -> Unit) {
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
            .height(94.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
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
                    coil.compose.AsyncImage(
                        model = imageModel,
                        contentDescription = "Foto de ${item.nome}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Item sem foto",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
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

                    // Tag adicional de Categoria para o Catálogo
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = item.categoria,
                            color = Color(0xFF475569),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
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
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver detalhes",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
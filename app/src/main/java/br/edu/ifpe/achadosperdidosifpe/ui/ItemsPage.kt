package br.edu.ifpe.achadosperdidosifpe.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.achadosperdidosifpe.model.Item
import br.edu.ifpe.achadosperdidosifpe.model.Status
import br.edu.ifpe.achadosperdidosifpe.model.Tipo

private val GreenFilter = Color(0xFF00913F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsPage(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onItemClick: (String) -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    val listaItensFicticios = remember { itens }

    val itensFiltrados = listaItensFicticios.filter { item ->
        val matchesSearch = item.nome.contains(searchText, ignoreCase = true) ||
                item.categoria.contains(searchText, ignoreCase = true)
        val matchesTab = when (selectedTab) {
            1 -> item.tipo == Tipo.PERDIDO
            2 -> item.tipo == Tipo.ENCONTRADO
            else -> true
        }
        matchesSearch && matchesTab
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Todos os Itens",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {  }) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filtrar Itens",
                    tint = GreenFilter
                )
            }
        }
        HorizontalDivider(color = Color(0xFFEEEEEE))
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = {
                    Text("Buscar por nome ou categoria...", fontSize = 13.sp, color = Color.Gray)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.Gray)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenFilter,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = GreenFilter,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = GreenFilter
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Todos", fontWeight = FontWeight.Medium, fontSize = 14.sp) },
                selectedContentColor = GreenFilter,
                unselectedContentColor = Color.Gray
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Perdidos", fontWeight = FontWeight.Medium, fontSize = 14.sp) },
                selectedContentColor = GreenFilter,
                unselectedContentColor = Color.Gray
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Encontrados", fontWeight = FontWeight.Medium, fontSize = 14.sp) },
                selectedContentColor = GreenFilter,
                unselectedContentColor = Color.Gray
            )
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum item corresponde   busca.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                itensFiltrados.forEach { item ->
                    ItemsPageCard(item = item, onClick = { onItemClick(item.id) })
                }
            }
        }
    }
}

@Composable
fun ItemsPageCard(item: Item, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(65.dp)
                    .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Item sem foto",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
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
                    val tagBgColor = if (isPerdido) Color(0xFFFCE8E6) else Color(0xFFE6F4EA)
                    val tagTextColor = if (isPerdido) Color(0xFFC5221F) else Color(0xFF137333)
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
                    color = Color.Black
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = " cone de localiza",
                        tint = Color.LightGray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.localizacao,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver detalhes",
                tint = Color.LightGray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
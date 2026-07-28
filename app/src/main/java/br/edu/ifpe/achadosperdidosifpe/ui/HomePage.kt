package br.edu.ifpe.achadosperdidosifpe.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.achadosperdidosifpe.R
import br.edu.ifpe.achadosperdidosifpe.model.Item
import br.edu.ifpe.achadosperdidosifpe.model.MainViewModel
import br.edu.ifpe.achadosperdidosifpe.model.Tipo
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreen
import coil.compose.SubcomposeAsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit = {},
    viewModel: MainViewModel,
    onLostItem: () -> Unit = {},
    onFindItem: () -> Unit = {},
    onSeeAllClick: () -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var currentFilter by remember { mutableStateOf(ItemFilter()) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val filteredItems = viewModel.items.filter { item ->
        val matchesSearch = item.nome.contains(searchText, ignoreCase = true) ||
                item.categoria.contains(searchText, ignoreCase = true)
        val matchesCategoria = currentFilter.categoria == null || item.categoria.equals(currentFilter.categoria, ignoreCase = true)
        val matchesTipo = currentFilter.tipo == null || item.tipo == currentFilter.tipo
        val matchesStatus = currentFilter.status == null || item.status == currentFilter.status
        val matchesCor = currentFilter.cor == null || item.corPrincipal?.contains(currentFilter.cor!!, ignoreCase = true) == true

        matchesSearch && matchesCategoria && matchesTipo && matchesStatus && matchesCor
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(scrollState)
    ) {
        // --- HERO HEADER INSTITUCIONAL ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = IfpeGreen,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ifpe_logomarca),
                        contentDescription = "Logo IFPE Pernambuco",
                        modifier = Modifier.height(48.dp)
                    )
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notificações",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Início & Painel",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Achados e Perdidos IFPE",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Conectando o campus para devolver o que é importante.",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Busca Rápida na Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = {
                            Text("Buscar por fone, chave, mochila...", fontSize = 13.sp, color = Color(0xFF6B7280))
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Buscar", tint = IfpeGreen)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    OutlinedButton(
                        onClick = { showFilterSheet = true },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (currentFilter.isActive) Color.White else Color.Transparent),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (currentFilter.isActive) Color(0xFFE8F5E9) else Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filtros",
                                tint = IfpeGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            if (currentFilter.isActive) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Ativo",
                                    color = IfpeGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- CONTEÚDO PRINCIPAL DA HOME ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Ações Rápidas
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Ações Rápidas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1E293B)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        onClick = onLostItem,
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color(0xFFFEE2E2), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                    contentDescription = "Perdi um item",
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Perdi um item", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF991B1B))
                            Text("Cadastre e procure", fontSize = 11.sp, color = Color(0xFFB91C1C))
                        }
                    }

                    Card(
                        onClick = onFindItem,
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = BorderStroke(1.dp, Color(0xFF86EFAC))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color(0xFFDCFCE7), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Encontrei um item",
                                    tint = IfpeGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Encontrei um item", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF166534))
                            Text("Ajude a devolver", fontSize = 11.sp, color = Color(0xFF15803D))
                        }
                    }
                }
            }

            // Atividades Recentes
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recém Registrados",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF1E293B)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onSeeAllClick() }
                    ) {
                        Text(
                            text = "Ver todos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = IfpeGreen
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Ver todos",
                            tint = IfpeGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (filteredItems.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhum item recente encontrado.",
                                color = Color(0xFF64748B),
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    filteredItems.take(3).forEach { item ->
                        ItemCard(item = item, onClick = { onItemClick(item.id) })
                    }
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
fun ItemCard(item: Item, onClick: () -> Unit) {
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
            .height(92.dp)
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
                        contentDescription = null,
                        tint = Color.LightGray
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
                        text = item.localizacaoFormatada,
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
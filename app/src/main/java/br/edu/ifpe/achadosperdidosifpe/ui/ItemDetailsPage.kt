package br.edu.ifpe.achadosperdidosifpe.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.achadosperdidosifpe.model.Item
import br.edu.ifpe.achadosperdidosifpe.model.Status
import br.edu.ifpe.achadosperdidosifpe.model.Tipo
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreen
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailsPage(
    item: Item? = null,
    onBackClick: () -> Unit = {},
    onChatClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val imageModel = remember(item?.fotoUrl) {
        if (item?.fotoUrl?.startsWith("data:image") == true) {
            try {
                val base64String = item.fotoUrl.substringAfter(",")
                android.util.Base64.decode(base64String, android.util.Base64.DEFAULT)
            } catch (e: Exception) {
                item.fotoUrl
            }
        } else {
            item?.fotoUrl
        }
    }

    val scrollState = rememberScrollState()
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR")) }

    val latLngItem = remember(item?.localizacao) {
        item?.localizacao?.let { loc ->
            if (loc.contains("Lat:") && loc.contains("Lng:")) {
                try {
                    val parts = loc.split(",")
                    val lat = parts[0].substringAfter("Lat:").trim().toDouble()
                    val lng = parts[1].substringAfter("Lng:").trim().toDouble()
                    LatLng(lat, lng)
                } catch (e: Exception) { null }
            } else null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalhes do Item",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IfpeGreen),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { innerPadding ->
        if (item == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Item não encontrado...", color = Color.Gray)
            }
        } else {
            val tagBgColor = if (item.tipo == Tipo.PERDIDO) Color(0xFFFCE8E6) else Color(0xFFE6F4EA)
            val tagTextColor = if (item.tipo == Tipo.PERDIDO) Color(0xFFC5221F) else Color(0xFF137333)
            val tagText = if (item.tipo == Tipo.PERDIDO) "Item Perdido" else "Item Encontrado"
            val tagIcon = if (item.tipo == Tipo.PERDIDO) Icons.Default.Warning else Icons.Default.CheckCircle

            val (statusBg, statusText, statusLabel) = when (item.status) {
                Status.NO_SETOR -> Triple(Color(0xFFFEF7E0), Color(0xFFB06000), "No Setor Oficial")
                Status.PERDIDO -> Triple(Color(0xFFF5F5F5), Color(0xFF5F6368), "Em Busca de Dono")
                Status.RESOLVIDO -> Triple(Color(0xFFE8F0FE), Color(0xFF1967D2), "Resolvido / Entregue")
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFC))
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (!item.fotoUrl.isNullOrEmpty()) {
                            coil.compose.AsyncImage(
                                model = imageModel,
                                contentDescription = "Foto de ${item.nome}",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Inbox,
                                contentDescription = "Sem Imagem",
                                modifier = Modifier.size(64.dp),
                                tint = Color.LightGray
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = tagBgColor, shape = RoundedCornerShape(16.dp)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(tagIcon, contentDescription = null, tint = tagTextColor, modifier = Modifier.size(16.dp))
                            Text(text = tagText, color = tagTextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Surface(color = statusBg, shape = RoundedCornerShape(16.dp)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Business, contentDescription = null, tint = statusText, modifier = Modifier.size(16.dp))
                            Text(text = statusLabel, color = statusText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = item.nome,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = item.descricao ?: "Nenhuma descrição detalhada fornecida.",
                        fontSize = 15.sp,
                        color = Color(0xFF475569)
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFDCFCE7),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = IfpeGreen, modifier = Modifier.size(20.dp))
                                }
                            }
                            Column {
                                Text("Localização Registrada", fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(item.localizacao, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            }
                        }

                        if (latLngItem != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                val cameraPos = rememberCameraPositionState {
                                    position = CameraPosition.fromLatLngZoom(latLngItem, 16f)
                                }
                                val markerState = rememberMarkerState(position = latLngItem)
                                GoogleMap(
                                    modifier = Modifier.fillMaxSize(),
                                    cameraPositionState = cameraPos,
                                    uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
                                ) {
                                    Marker(state = markerState, title = item.nome)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    val gmmIntentUri = Uri.parse("geo:${latLngItem.latitude},${latLngItem.longitude}?q=${latLngItem.latitude},${latLngItem.longitude}(${item.nome})")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    context.startActivity(mapIntent)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, IfpeGreen)
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null, tint = IfpeGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Abrir Rota no Google Maps", color = IfpeGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(20.dp))
                                }
                            }
                            Column {
                                Text("Data do Registro", fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(
                                    text = item.data?.let { dateFormatter.format(it) } ?: "-",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Informações Adicionais", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Categoria:", fontSize = 14.sp, color = Color(0xFF64748B))
                            Text(item.categoria, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                        }
                        item.corPrincipal?.let { cor ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Cor Principal:", fontSize = 14.sp, color = Color(0xFF64748B))
                                Text(cor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                            }
                        }
                    }
                }

                if (item.status != Status.RESOLVIDO) {
                    Button(
                        onClick = onChatClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IfpeGreen)
                    ) {
                        Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Entrar em Contato via Chat", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
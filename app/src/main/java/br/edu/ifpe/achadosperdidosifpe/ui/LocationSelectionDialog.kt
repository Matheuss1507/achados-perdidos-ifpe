package br.edu.ifpe.achadosperdidosifpe.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreen
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.util.Locale

data class SetorCampus(val nome: String, val cor: Color)

val listaSetoresCampus = listOf(
    SetorCampus("Bloco A", Color(0xFF3B82F6)),
    SetorCampus("Bloco B", Color(0xFF3B82F6)),
    SetorCampus("Bloco C", Color(0xFF3B82F6)),
    SetorCampus("Bloco D", Color(0xFF3B82F6)),
    SetorCampus("Biblioteca", Color(0xFFA855F7)),
    SetorCampus("Cantina", Color(0xFFF97316)),
    SetorCampus("Quadra Esportiva", Color(0xFF22C55E)),
    SetorCampus("Estacionamento", Color(0xFF64748B)),
    SetorCampus("Portaria", Color(0xFFEF4444)),
    SetorCampus("Laboratórios", Color(0xFF6366F1))
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LocationSelectionDialog(
    initialLocation: String = "",
    hasLocationPermission: Boolean = false,
    onRequestPermission: () -> Unit = {},
    onDismissRequest: () -> Unit,
    onLocationSelected: (String) -> Unit
) {
    var localSelecionadoTexto by remember { mutableStateOf(initialLocation) }
    var pontoMapaSelecionado by remember { mutableStateOf<LatLng?>(null) }
    var setorSelecionado by remember { mutableStateOf<SetorCampus?>(null) }

    val defaultPos = LatLng(-8.0522, -34.9286)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPos, 17f)
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(20.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // --- CABEÇALHO ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFDCFCE7),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = IfpeGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            text = "Selecionar Localização",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = Color(0xFF64748B)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                // --- CONTEÚDO SCROLLÁVEL ---
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NearMe,
                                    contentDescription = null,
                                    tint = IfpeGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Clique no local onde você perdeu ou encontrou o item",
                                    fontSize = 13.sp,
                                    color = Color(0xFF166534),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (hasLocationPermission) {
                                            pontoMapaSelecionado = defaultPos
                                            localSelecionadoTexto = "Lat: -8.0522, Lng: -34.9286"
                                        } else {
                                            onRequestPermission()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = IfpeGreen),
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text("Usar minha localização", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                OutlinedButton(
                                    onClick = {
                                        setorSelecionado = null
                                    },
                                    modifier = Modifier.weight(1f),
                                    border = BorderStroke(1.dp, IfpeGreen),
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text("Abrir no Google Maps", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IfpeGreen)
                                }
                            }
                        }
                    }

                    // --- CONTAINER DO MAPA GOOGLE MAPS ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(16.dp))
                    ) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                            uiSettings = MapUiSettings(myLocationButtonEnabled = true, zoomControlsEnabled = false),
                            onMapClick = { latLng ->
                                pontoMapaSelecionado = latLng
                                setorSelecionado = null
                                localSelecionadoTexto = String.format(Locale.US, "Lat: %.5f, Lng: %.5f", latLng.latitude, latLng.longitude)
                            }
                        ) {
                            pontoMapaSelecionado?.let { latLng ->
                                val markerState = rememberMarkerState(position = latLng)
                                Marker(
                                    state = markerState,
                                    title = "Local Selecionado"
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.TopStart),
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 2.dp
                        ) {
                            Text(
                                text = "Campus IFPE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // --- GRADE DE SETORES DO CAMPUS ---
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listaSetoresCampus.forEach { setor ->
                            val isSelected = setorSelecionado?.nome == setor.nome
                            Surface(
                                modifier = Modifier.clickable {
                                    setorSelecionado = setor
                                    localSelecionadoTexto = setor.nome
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFFDCFCE7) else Color.White,
                                border = BorderStroke(1.dp, if (isSelected) IfpeGreen else Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(setor.cor, CircleShape)
                                    )
                                    Text(
                                        text = setor.nome,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) IfpeGreen else Color(0xFF1E293B)
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                // --- RODAPÉ COM BOTÕES DE AÇÃO ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Text("Cancelar", color = Color(0xFF475569), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            onLocationSelected("Fora do Campus")
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF94A3B8))
                    ) {
                        Text("Fora do Campus", color = Color(0xFF334155), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            if (localSelecionadoTexto.isNotBlank()) {
                                onLocationSelected(localSelecionadoTexto)
                            }
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IfpeGreen)
                    ) {
                        Text("Confirmar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
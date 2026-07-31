package br.edu.ifpe.achadosperdidosifpe.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.runtime.*
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
import br.edu.ifpe.achadosperdidosifpe.model.MainViewModel
import br.edu.ifpe.achadosperdidosifpe.model.Status
import br.edu.ifpe.achadosperdidosifpe.model.Tipo
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreen
import coil.compose.SubcomposeAsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailsPage(
    item: Item? = null,
    viewModel: MainViewModel,
    onBackClick: () -> Unit = {},
    onChatClick: (chatId: String) -> Unit = {}
) {
    val context = LocalContext.current
    val currentUserId = viewModel.user?.id
    val isCriador = item?.usuarioId == currentUserId

    var respostaTexto by remember(item?.respostaVerificacao) { mutableStateOf(item?.respostaVerificacao ?: "") }
    var isEnviandoResposta by remember { mutableStateOf(false) }

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

    val latLngItem = remember(item?.latitude, item?.longitude) {
        if (item?.latitude != null && item.longitude != null) {
            LatLng(item.latitude, item.longitude)
        } else null
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Item", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
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
            val isResolvido = item.status == Status.RESOLVIDO

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
                // Banner de Alerta se o Item estiver Resolvido
                if (isResolvido) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE)),
                        border = BorderStroke(1.dp, Color(0xFF0284C7)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF0284C7))
                            Text(
                                text = "Este item já foi DEVOLVIDO/RESOLVIDO. O chat e as interações foram desativados.",
                                color = Color(0xFF0369A1),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Foto do Item
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
                            SubcomposeAsyncImage(
                                model = imageModel,
                                contentDescription = "Foto de ${item.nome}",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                loading = {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = IfpeGreen, modifier = Modifier.size(28.dp))
                                    }
                                },
                                error = {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.BrokenImage, contentDescription = "Erro ao carregar foto", tint = Color.Gray)
                                    }
                                }
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

                // Badges de Tipo e Status
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

                // Nome e Descrição
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = item.nome, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text(text = item.descricao ?: "Nenhuma descrição detalhada fornecida.", fontSize = 15.sp, color = Color(0xFF475569))
                }

                // Localização Estruturada (Setor + Coordenadas)
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFDCFCE7),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = IfpeGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text("Localização Registrada", fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(
                                    item.localizacaoFormatada,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }

                        if (latLngItem != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                val cameraPos = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(latLngItem, 16f) }
                                GoogleMap(
                                    modifier = Modifier.fillMaxSize(),
                                    cameraPositionState = cameraPos,
                                    uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
                                ) {
                                    Marker(state = rememberMarkerState(position = latLngItem), title = item.nome)
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                abrirNoGoogleMaps(
                                    context = context,
                                    setor = item.setor,
                                    latitude = item.latitude,
                                    longitude = item.longitude
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, IfpeGreen)
                        ) {
                            Icon(
                                Icons.Default.Map,
                                contentDescription = null,
                                tint = IfpeGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Abrir no Google Maps",
                                color = IfpeGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Informações Adicionais
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
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Data do Registro:", fontSize = 14.sp, color = Color(0xFF64748B))
                            Text(
                                text = item.data?.let { dateFormatter.format(it) } ?: "-",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                }

                // --- SEÇÃO: PERGUNTA DE VERIFICAÇÃO & CONFIRMAÇÃO DE DEVOLUÇÃO ---
                if (!item.perguntaVerificacao.isNullOrBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        border = BorderStroke(1.dp, Color(0xFFFCD34D)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.HelpOutline, contentDescription = null, tint = Color(0xFFD97706))
                                Text("Pergunta de Verificação", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF92400E))
                            }

                            Text(
                                text = item.perguntaVerificacao,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF78350F)
                            )

                            // 1. Visão do Solicitante: Enviar Resposta (se ainda não resolvido)
                            if (!isCriador && !isResolvido) {
                                OutlinedTextField(
                                    value = respostaTexto,
                                    onValueChange = { respostaTexto = it },
                                    placeholder = { Text("Digite sua resposta para comprovar a posse...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    enabled = item.respostaVerificacao == null && !isEnviandoResposta
                                )

                                if (item.respostaVerificacao == null) {
                                    Button(
                                        onClick = {
                                            if (respostaTexto.isNotBlank()) {
                                                isEnviandoResposta = true
                                                viewModel.responderPerguntaVerificacao(item, respostaTexto) { sucesso ->
                                                    isEnviandoResposta = false
                                                    if (sucesso) Toast.makeText(context, "Resposta enviada!", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        },
                                        enabled = !isEnviandoResposta && respostaTexto.isNotBlank(),
                                        colors = ButtonDefaults.buttonColors(containerColor = IfpeGreen)
                                    ) {
                                        Text("Enviar Resposta")
                                    }
                                } else {
                                    Text("Resposta enviada! Aguardando validação do anunciante.", fontSize = 12.sp, color = IfpeGreen, fontWeight = FontWeight.Bold)
                                }
                            }

                            // 2. Visão do Criador do Anúncio: Validar Resposta & Confirmar Devolução
                            if (isCriador) {
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Resposta do Solicitante:", fontSize = 12.sp, color = Color(0xFF92400E))
                                        Text(
                                            text = item.respostaVerificacao ?: "Nenhuma resposta enviada ainda.",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (item.respostaVerificacao != null) Color(0xFF15803D) else Color.Gray
                                        )
                                    }
                                }

                                if (!isResolvido) {
                                    Button(
                                        onClick = {
                                            viewModel.confirmarDevolucao(item) { sucesso ->
                                                if (sucesso) {
                                                    Toast.makeText(context, "Devolução confirmada! Status atualizado.", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth().height(48.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = IfpeGreen)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Confirmar Devolução", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Botão de Chat (Desativado caso o status seja RESOLVIDO)
                if (!isResolvido) {
                    Button(
                        onClick = {
                            if (!isCriador) {
                                android.util.Log.d("CHAT_DEBUG", "itemId: ${item.id}, ownerId: ${item.usuarioId}")
                                viewModel.openOrCreateChat(
                                    itemId = item.id,
                                    ownerId = item.usuarioId
                                ) { chatId ->
                                    android.util.Log.d("CHAT_DEBUG", "chatId criado: $chatId")

                                    onChatClick(chatId)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
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

private fun abrirNoGoogleMaps(
    context: Context,
    setor: String?,
    latitude: Double?,
    longitude: Double?
) {
    if (latitude == null && longitude == null && setor.isNullOrBlank()) return

    val label = setor?.ifBlank { "Local do Item" } ?: "Local do Item"

    val intentUri = if (latitude != null && longitude != null) {
        Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(label)})")
    } else {
        Uri.parse("geo:0,0?q=${Uri.encode(setor)}")
    }

    val mapIntent = Intent(Intent.ACTION_VIEW, intentUri).apply {
        setPackage("com.google.android.apps.maps")
    }

    try {
        context.startActivity(mapIntent)
    } catch (e: Exception) {
        val webUrl = if (latitude != null && longitude != null) {
            "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
        } else {
            "https://www.google.com/maps/search/?api=1&query=${Uri.encode(setor)}"
        }
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
        context.startActivity(browserIntent)
    }
}
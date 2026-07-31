package br.edu.ifpe.achadosperdidosifpe.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import br.edu.ifpe.achadosperdidosifpe.db.fb.DatabaseProvider
import br.edu.ifpe.achadosperdidosifpe.db.fb.FBChat
import br.edu.ifpe.achadosperdidosifpe.model.MainViewModel
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreen
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreenMid
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    chatIdInicial: String? = null,
    onBackClick: () -> Unit = {}
) {
    val chatInicialVM = remember { viewModel.consumirChatInicial() }
    var chatSelecionadoId by remember(chatIdInicial, chatInicialVM) {
        mutableStateOf(chatIdInicial ?: chatInicialVM)
    }

    val context = LocalContext.current
    val currentUserId = Firebase.auth.currentUser?.uid ?: ""
    val formatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    var mensagemAtual by remember { mutableStateOf("") }

    val listState = rememberLazyListState()
    val fbChats by remember { derivedStateOf { viewModel.chats } }
    val fbMessages by remember { derivedStateOf { viewModel.messages } }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Garante escuta contínua de conversas do usuário
    LaunchedEffect(Unit) {
        viewModel.startListeningChats()
    }

    // Gerencia o listener das mensagens do chat selecionado
    LaunchedEffect(chatSelecionadoId) {
        if (chatSelecionadoId != null) {
            viewModel.startListeningMessages(chatSelecionadoId!!)
        } else {
            viewModel.stopListeningMessages()
        }
    }

    // Auto-scroll para a última mensagem
    LaunchedEffect(fbMessages.size) {
        if (fbMessages.isNotEmpty()) {
            listState.animateScrollToItem(fbMessages.size - 1)
        }
    }

    // Extrai o chat do estado global ou cria objeto temporário resiliente
    val chatAtualFB = if (chatSelecionadoId != null) {
        fbChats.find { it.id == chatSelecionadoId } ?: run {
            val partes = chatSelecionadoId!!.split("_")
            val itemId = partes.firstOrNull() ?: ""
            val participantes = if (partes.size >= 3) partes.drop(1) else emptyList()
            FBChat(
                id = chatSelecionadoId!!,
                itemId = itemId,
                participants = participantes
            )
        }
    } else null

    // Identifica o ID do destinatário
    val outroParticipanteId = remember(chatAtualFB, currentUserId) {
        chatAtualFB?.participants?.firstOrNull { it != currentUserId } ?: ""
    }

    // Busca o nome do destinatário no banco
    var nomeDestinatario by remember(outroParticipanteId) { mutableStateOf("") }

    LaunchedEffect(outroParticipanteId) {
        if (outroParticipanteId.isNotBlank()) {
            DatabaseProvider.database.getUser(outroParticipanteId) { user ->
                if (user != null && user.nome.isNotBlank()) {
                    nomeDestinatario = user.nome
                } else {
                    nomeDestinatario = "Usuário"
                }
            }
        } else {
            nomeDestinatario = ""
        }
    }

    @SuppressLint("MissingPermission")
    fun capturarEEnviarLocalizacao() {
        if (chatSelecionadoId == null) return
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val texto = "📍 Localização compartilhada:\nhttps://maps.google.com/?q=${location.latitude},${location.longitude}"
                viewModel.sendMessage(chatSelecionadoId!!, texto)
                Toast.makeText(context, "Localização enviada!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "GPS indisponível.", Toast.LENGTH_LONG).show()
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) capturarEEnviarLocalizacao()
        else Toast.makeText(context, "Permissão negada.", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = {
                    if (chatAtualFB != null) {
                        Column {
                            Text(
                                text = "Chat",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            if (nomeDestinatario.isNotBlank()) {
                                Text(
                                    text = nomeDestinatario,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else {
                        Text("Mensagens", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                },
                navigationIcon = {
                    if (chatAtualFB != null) {
                        IconButton(onClick = { chatSelecionadoId = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IfpeGreen),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { innerPadding ->
        if (chatAtualFB != null) {
            // Conversa Ativa
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFFF9F9F9))
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0FE)),
                        border = BorderStroke(1.dp, Color.LightGray),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val nomeItem = viewModel.items.find { it.id == chatAtualFB.itemId }?.nome ?: ""
                            Text("Chat sobre: ", fontSize = 14.sp, color = Color.Black)
                            Text(nomeItem, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                        }
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(fbMessages, key = { it.id }) { msg ->
                            val isMe = msg.senderId == currentUserId
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isMe) IfpeGreenMid else Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(1.dp),
                                    modifier = Modifier.widthIn(max = 280.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = msg.text,
                                            fontSize = 14.sp,
                                            color = if (isMe) Color.White else Color.Black
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = formatter.format(Date(msg.timestamp)),
                                            fontSize = 11.sp,
                                            color = if (isMe) Color.White.copy(alpha = 0.7f) else Color.Gray,
                                            modifier = Modifier.align(Alignment.End)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).clickable {
                            val granted = ContextCompat.checkSelfPermission(
                                context, android.Manifest.permission.ACCESS_FINE_LOCATION
                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                            if (granted) capturarEEnviarLocalizacao()
                            else locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        },
                        border = BorderStroke(1.dp, Color(0xFFDDDDDD)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = IfpeGreenMid)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Compartilhar Localização em Tempo Real", color = IfpeGreenMid, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = mensagemAtual,
                        onValueChange = { mensagemAtual = it },
                        placeholder = { Text("Digite uma mensagem...", fontSize = 14.sp, color = Color.Gray) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = CircleShape,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF1F1F1),
                            focusedContainerColor = Color(0xFFF1F1F1),
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    FilledIconButton(
                        onClick = {
                            if (mensagemAtual.isNotBlank()) {
                                viewModel.sendMessage(chatSelecionadoId!!, mensagemAtual)
                                mensagemAtual = ""
                            }
                        },
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = IfpeGreenMid)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.White)
                    }
                }
            }
        } else {
            // Lista de Conversas
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFFF9F9F9))
                    .padding(innerPadding)
            ) {
                if (fbChats.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Nenhuma conversa ainda.", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    fbChats.forEach { chat ->
                        val outroParticipanteIdCard = chat.participants.firstOrNull { it != currentUserId } ?: ""
                        var nomeExibido by remember(outroParticipanteIdCard) { mutableStateOf("Usuário") }

                        LaunchedEffect(outroParticipanteIdCard) {
                            if (outroParticipanteIdCard.isNotBlank()) {
                                DatabaseProvider.database.getUser(outroParticipanteIdCard) { user ->
                                    if (user != null && user.nome.isNotBlank()) {
                                        nomeExibido = user.nome
                                    }
                                }
                            }
                        }

                        val iniciais = remember(nomeExibido) {
                            val partes = nomeExibido.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
                            when {
                                partes.size >= 2 -> "${partes.first().first().uppercase()}${partes.last().first().uppercase()}"
                                partes.size == 1 && partes[0].isNotEmpty() -> partes[0].take(2).uppercase()
                                else -> "US"
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .clickable { chatSelecionadoId = chat.id }
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Color(0xFFE8F5E9), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(iniciais, color = IfpeGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = nomeExibido,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.Black,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = if (chat.lastTimestamp > 0L)
                                                formatter.format(Date(chat.lastTimestamp))
                                            else "",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    val nomeItem = viewModel.items.find { it.id == chat.itemId }?.nome ?: ""
                                    Text("Item: $nomeItem", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = IfpeGreenMid, maxLines = 1)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(chat.lastMessage, fontSize = 13.sp, color = Color.Gray, maxLines = 1)
                                }
                            }
                        }
                        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                    }
                }
            }
        }
    }
}
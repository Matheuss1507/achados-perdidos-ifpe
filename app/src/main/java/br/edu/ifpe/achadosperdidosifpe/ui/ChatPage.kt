package br.edu.ifpe.achadosperdidosifpe.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.achadosperdidosifpe.model.Chat
import br.edu.ifpe.achadosperdidosifpe.model.Message
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreen
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreenMid
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPage(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    var idChatSelecionado by remember { mutableStateOf<String?>(null) }
    var mensagemAtual by remember { mutableStateOf("") }
    val estadoRolagem = rememberScrollState()

    val listaChats = remember {
        listOf(
            Chat(
                id = "1",
                nome = "João Silva",
                papel = "Estudante - Informática",
                tituloItem = "Celular Samsung Galaxy",
                ultimaMensagem = "Perfeito! é esse mesmo. Deixei no Setor...",
                horario = "14:37"
            ),
            Chat(
                id = "2",
                nome = "Maria Souza",
                papel = "Estudante - Redes",
                tituloItem = "Garrafa térmica azul",
                ultimaMensagem = "Olá! Você encontrou minha garrafa?",
                horario = "Ontem"
            )
        )
    }

    val mapaMensagens = remember {
        mapOf(
            "1" to listOf(
                Message("Olá! Vi que você encontrou meu celular. Muito obrigado!", "14:32", false),
                Message("Oi! Sim, encontrei perto da cantina. Para confirmar que é seu, pode responder: Qual é o papel de parede do celular?", "14:35", true),
                Message("É uma foto da minha família na praia!", "14:36", false),
                Message("Perfeito! É esse mesmo. Deixei no Setor de Achados e Perdidos. Fica no Bloco A.", "14:37", true)
            ),
            "2" to listOf(
                Message("Olá! Você encontrou minha garrafa?", "Ontem", false),
                Message("Oi, Maria! Encontrei sim, estava na Quadra Poliesportiva próximo à arquibancada.", "Ontem", true),
                Message("Nossa, que alívio! Posso passar para pegar amanhã ?", "Ontem", false)
            )
        )
    }

    val chatAtual = listaChats.find { it.id == idChatSelecionado }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = {
                    if (chatAtual != null) {
                        Column {
                            Text(
                                text = chatAtual.nome,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = chatAtual.papel,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Text(
                            text = "Mensagens",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                navigationIcon = {
                    if (chatAtual != null) {
                        IconButton(onClick = { idChatSelecionado = null }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IfpeGreen),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { innerPadding ->
        if (chatAtual != null) {
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0FE)),
                        border = BorderStroke(1.dp, Color.LightGray),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Chat sobre: ", fontSize = 14.sp, color = Color.Black)
                            Text(chatAtual.tituloItem, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(estadoRolagem)
                    ) {
                        val mensagensAtuais = mapaMensagens[chatAtual.id] ?: emptyList()
                        mensagensAtuais.forEach { msg ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                horizontalArrangement = if (msg.enviadoPorMim) Arrangement.End else Arrangement.Start
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (msg.enviadoPorMim) IfpeGreenMid else Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(1.dp),
                                    modifier = Modifier.widthIn(max = 280.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = msg.texto,
                                            fontSize = 14.sp,
                                            color = if (msg.enviadoPorMim) Color.White else Color.Black
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = msg.horario,
                                            fontSize = 11.sp,
                                            color = if (msg.enviadoPorMim) Color.White.copy(alpha = 0.7f) else Color.Gray,
                                            modifier = Modifier.align(Alignment.End)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        border = BorderStroke(1.dp, Color(0xFFDDDDDD)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = IfpeGreenMid
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Compartilhar Localização em Tempo Real",
                                color = IfpeGreenMid,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = mensagemAtual,
                        onValueChange = { mensagemAtual = it },
                        placeholder = {
                            Text(
                                "Digite uma mensagem...",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
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
                        onClick = { if (mensagemAtual.isNotBlank()) mensagemAtual = "" },
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = IfpeGreenMid)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Enviar",
                            tint = Color.White
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFFF9F9F9))
                    .padding(innerPadding)
            ) {
                listaChats.forEach { chat ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .clickable { idChatSelecionado = chat.id }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFFE8F5E9), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                val iniciais = chat.nome.split(" ").map { it.take(1) }.joinToString("")
                                Text(
                                    text = iniciais,
                                    color = IfpeGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = chat.nome,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = chat.horario,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Item: ${chat.tituloItem}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = IfpeGreenMid,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = chat.ultimaMensagem,
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                }
            }
        }
    }
}
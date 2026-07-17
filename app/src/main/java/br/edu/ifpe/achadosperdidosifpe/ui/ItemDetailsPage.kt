package br.edu.ifpe.achadosperdidosifpe.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.achadosperdidosifpe.model.Item
import br.edu.ifpe.achadosperdidosifpe.model.Status
import br.edu.ifpe.achadosperdidosifpe.model.Tipo
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailsPage(
    item: Item? = null,
    onBackClick: () -> Unit = {},
    onChatClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR")) }

    val ifpeGreen = Color(0xFF00642F)
    val alertBlueBg = Color(0xFFE8F0FE)
    val alertBlueText = Color(0xFF1967D2)
    val instructionBg = Color(0xFFFFF8E1)
    val instructionBorder = Color(0xFFFFE082)
    val actionButtonColor = Color(0xFFB57C1E)

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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ifpeGreen),
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
                Text(text = "Item não encontrado ou carregando...", color = Color.Gray)
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
                    .background(Color(0xFFF9F9F9))
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFFF2F2F2), shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!item.fotoUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = item.fotoUrl,
                            contentDescription = "Foto de ${item.nome}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = "Sem Imagem",
                            modifier = Modifier.size(72.dp),
                            tint = Color.LightGray
                        )
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
                            Text(text = tagText, color = tagTextColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Surface(color = statusBg, shape = RoundedCornerShape(16.dp)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Business, contentDescription = null, tint = statusText, modifier = Modifier.size(16.dp))
                            Text(text = statusLabel, color = statusText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = item.nome,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = item.descricao ?: "Nenhuma descrição detalhada fornecida.",
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.size(36.dp).background(Color(0xFFF5F5F5), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray)
                            }
                            Column {
                                Text("Localização de Referência", fontSize = 12.sp, color = Color.Gray)
                                Text(item.localizacao, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF5F5F5))

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.size(36.dp).background(Color(0xFFF5F5F5), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.Gray)
                            }
                            Column {
                                Text("Data do Registro", fontSize = 12.sp, color = Color.Gray)
                                Text(dateFormatter.format(item.data), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Informações Adicionais", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Categoria:", fontSize = 14.sp, color = Color.Gray)
                            Text(item.categoria, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                        }
                        item.corPrincipal?.let { cor ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Cor Principal:", fontSize = 14.sp, color = Color.Gray)
                                Text(cor, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                            }
                        }
                    }
                }

                if (item.tipo == Tipo.ENCONTRADO && !item.perguntaVerificacao.isNullOrEmpty() && item.status != Status.RESOLVIDO) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = alertBlueBg),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFD2E3FC))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = alertBlueText)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Pergunta de Verificação", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = alertBlueText)
                                Text(item.perguntaVerificacao, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                                Text("Você precisará responder esta pergunta no setor para confirmar a posse.", fontSize = 13.sp, color = alertBlueText)
                            }
                        }
                    }
                }

                when {
                    item.status == Status.RESOLVIDO -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F4EA)),
                            border = BorderStroke(1.dp, Color(0xFF34A853)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF137333))
                                Text(
                                    text = "Este item já foi devolvido e o caso está devidamente encerrado. Obrigado a todos!",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF137333)
                                )
                            }
                        }
                    }
                    item.tipo == Tipo.ENCONTRADO && item.status == Status.NO_SETOR -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = instructionBg),
                            border = BorderStroke(1.dp, instructionBorder),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFFB06000))
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Instruções para Retirada", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB06000))
                                    val itensInstrucoes = listOf(
                                        "Dirija-se ao Setor de Achados e Perdidos do IFPE",
                                        "Horário: Segunda a Sexta, 8h às 17h",
                                        "Leve um documento oficial com foto",
                                        "Esteja preparado para validar as características do item"
                                    )
                                    itensInstrucoes.forEach { instrucao ->
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text("•", fontWeight = FontWeight.Bold, color = Color.Black)
                                            Text(instrucao, fontSize = 14.sp, color = Color.DarkGray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item.tipo == Tipo.PERDIDO -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE8E6)),
                            border = BorderStroke(1.dp, Color(0xFFF5C2C1)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFC5221F))
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Como Ajudar?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC5221F))
                                    Text("Se você encontrou ou viu este item em algum lugar do campus, entre em contato imediatamente com o dono pelo chat integrado.", fontSize = 14.sp, color = Color.DarkGray)
                                }
                            }
                        }
                    }
                }

                if (item.status != Status.RESOLVIDO) {
                    val isPerdido = item.tipo == Tipo.PERDIDO
                    Button(
                        onClick = { if (isPerdido) onChatClick() else { } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPerdido) ifpeGreen else actionButtonColor
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = if (isPerdido) Icons.Default.ChatBubbleOutline else Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isPerdido) "Entrar em Contato via Chat" else "Ver Localização do Setor",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AsyncImage(
    model: String,
    contentDescription: String,
    modifier: Modifier,
    contentScale: ContentScale
) {
    TODO("Not yet implemented")
}
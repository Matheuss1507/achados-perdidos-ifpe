package br.edu.ifpe.achadosperdidosifpe.ui

import android.widget.Toast
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import br.edu.ifpe.achadosperdidosifpe.model.MainViewModel
import br.edu.ifpe.achadosperdidosifpe.model.Item
import br.edu.ifpe.achadosperdidosifpe.model.Tipo
import br.edu.ifpe.achadosperdidosifpe.model.Status
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreen
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreenMid
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostItemPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToItems: () -> Unit = {}
) {
    var nome by remember { mutableStateOf("") }
    var categoriaExpanded by remember { mutableStateOf(false) }
    var categoriaSelecionada by remember { mutableStateOf("") }
    var corPrincipal by remember { mutableStateOf("") }
    var localizacao by remember { mutableStateOf("") }
    var data by remember { mutableStateOf("") }
    var caracteristicas by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var fotoUrl by remember { mutableStateOf<String?>(null) }
    var showMapDialog by remember { mutableStateOf(false) }
    var showPhotoOptions by remember { mutableStateOf(false) }
    var isSalvando by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        showMapDialog = true
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            fotoUrl = uri.toString()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            try {
                val file = File(context.cacheDir, "lost_${System.currentTimeMillis()}.jpg")
                FileOutputStream(file).use { out ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
                }
                fotoUrl = file.absolutePath
            } catch (e: Exception) {
                Toast.makeText(context, "Erro ao salvar foto da câmera", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val categorias = listOf("Documentos", "Eletrônicos", "Acessórios", "Vestuário", "Material escolar", "Outros")
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = IfpeGreen
                )
            }
            Text(
                text = "Perdi um item",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        HorizontalDivider(color = Color(0xFFEEEEEE))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = IfpeGreenMid,
                    modifier = Modifier.size(15.dp)
                )
                Text("Foto de Referência (opcional)", fontSize = 13.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .border(1.5.dp, Color(0xFFBBBBBB), RoundedCornerShape(10.dp))
                    .clickable { if (!isSalvando) showPhotoOptions = true },
                contentAlignment = Alignment.Center
            ) {
                if (fotoUrl != null) {
                    coil.compose.AsyncImage(
                        model = fotoUrl,
                        contentDescription = "Foto selecionada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(36.dp)
                        )
                        Text("Clique para adicionar foto do item", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }

            FormField(label = "O que você perdeu?") {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    placeholder = { Text("Ex: Caderno de Cálculo", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true,
                    enabled = !isSalvando
                )
            }

            FormField(label = "Categoria") {
                ExposedDropdownMenuBox(
                    expanded = categoriaExpanded,
                    onExpandedChange = { if (!isSalvando) categoriaExpanded = it }
                ) {
                    OutlinedTextField(
                        value = categoriaSelecionada,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Selecione...", color = Color.LightGray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(10.dp),
                        colors = fieldColors(),
                        enabled = !isSalvando
                    )
                    ExposedDropdownMenu(
                        expanded = categoriaExpanded,
                        onDismissRequest = { categoriaExpanded = false }
                    ) {
                        categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria) },
                                onClick = {
                                    categoriaSelecionada = categoria
                                    categoriaExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            FormField(label = "Cor Principal", icon = Icons.Default.Palette) {
                OutlinedTextField(
                    value = corPrincipal,
                    onValueChange = { corPrincipal = it },
                    placeholder = { Text("Ex: Azul escuro", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true,
                    enabled = !isSalvando
                )
            }

            HorizontalDivider(color = Color(0xFFEEEEEE))

            FormField(label = "Onde perdeu?", icon = Icons.Default.LocationOn) {
                OutlinedTextField(
                    value = localizacao,
                    onValueChange = { localizacao = it },
                    placeholder = { Text("Selecionar no mapa...", color = Color.LightGray) },
                    trailingIcon = {
                        IconButton(
                            enabled = !isSalvando,
                            onClick = {
                                if (!hasLocationPermission) {
                                    permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                                showMapDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "Abrir mapa",
                                tint = IfpeGreenMid,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true,
                    enabled = !isSalvando
                )
            }

            FormField(label = "Quando perdeu?", icon = Icons.Default.CalendarMonth) {
                OutlinedTextField(
                    value = data,
                    onValueChange = { data = it },
                    placeholder = { Text("dd/mm/aaaa", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true,
                    enabled = !isSalvando
                )
            }

            HorizontalDivider(color = Color(0xFFEEEEEE))

            FormField(label = "Características únicas", icon = Icons.Default.Fingerprint) {
                OutlinedTextField(
                    value = caracteristicas,
                    onValueChange = { caracteristicas = it },
                    placeholder = { Text("Ex: Tem um adesivo do Python, página 47 marcada.", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth().height(96.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    maxLines = 4,
                    enabled = !isSalvando
                )
            }

            FormField(label = "Descrição Adicional") {
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    placeholder = { Text("Qualquer informação adicional...", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    maxLines = 3,
                    enabled = !isSalvando
                )
            }

            Button(
                onClick = {
                    if (nome.isBlank() || categoriaSelecionada.isBlank() || localizacao.isBlank() || data.isBlank()) {
                        Toast.makeText(context, "Por favor, preencha os campos obrigatórios (*)", Toast.LENGTH_SHORT).show()
                    } else {
                        isSalvando = true
                        val parsedDate = try {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(data) ?: Date()
                        } catch (e: Exception) {
                            Date()
                        }

                        val item = Item(
                            id = UUID.randomUUID().toString(),
                            usuarioId = viewModel.user?.id ?: "user_anonimo",
                            tipo = Tipo.PERDIDO,
                            status = Status.PERDIDO,
                            nome = nome,
                            categoria = categoriaSelecionada,
                            corPrincipal = corPrincipal.ifBlank { null },
                            localizacao = localizacao,
                            caracteristicasUnicas = caracteristicas.ifBlank { null },
                            descricao = descricao.ifBlank { null },
                            fotoUrl = null,
                            data = parsedDate
                        )

                        viewModel.addItemComFoto(item, fotoUrl) { sucesso ->
                            isSalvando = false
                            if (sucesso) {
                                Toast.makeText(context, "Item perdido registrado!", Toast.LENGTH_SHORT).show()
                                onNavigateToItems()
                            } else {
                                Toast.makeText(context, "Falha ao enviar dados ou imagem.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                enabled = !isSalvando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IfpeGreen)
            ) {
                if (isSalvando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Registrar item perdido",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }

    if (showPhotoOptions) {
        AlertDialog(
            onDismissRequest = { showPhotoOptions = false },
            title = { Text("Adicionar Foto") },
            text = { Text("Selecione se deseja tirar uma foto com a câmera ou escolher um arquivo da sua galeria.") },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoOptions = false
                    cameraLauncher.launch(null)
                }) { Text("Câmera", color = IfpeGreen) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoOptions = false
                    galleryLauncher.launch("image/*")
                }) { Text("Galeria", color = IfpeGreen) }
            }
        )
    }

    if (showMapDialog) {
        Dialog(onDismissRequest = { showMapDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().height(450.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        val cameraPositionState = rememberCameraPositionState()
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                            uiSettings = MapUiSettings(myLocationButtonEnabled = true),
                            onMapClick = { latLng ->
                                localizacao = "Lat: ${String.format(Locale.US, "%.4f", latLng.latitude)}, Lng: ${String.format(Locale.US, "%.4f", latLng.longitude)}"
                                showMapDialog = false
                            }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showMapDialog = false }) {
                            Text("Cancelar", color = IfpeGreen)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormField(
    label: String,
    icon: ImageVector? = null,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = IfpeGreenMid,
                    modifier = Modifier.size(15.dp)
                )
            }
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
        }
        content()
    }
}

@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = IfpeGreen,
    unfocusedBorderColor = Color(0xFFDDDDDD),
    focusedLabelColor = IfpeGreen,
    unfocusedContainerColor = Color.White,
    focusedContainerColor = Color.White
)
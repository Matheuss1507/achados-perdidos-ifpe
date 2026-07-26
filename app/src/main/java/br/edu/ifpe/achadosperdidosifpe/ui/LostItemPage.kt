package br.edu.ifpe.achadosperdidosifpe.ui

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import br.edu.ifpe.achadosperdidosifpe.model.Item
import br.edu.ifpe.achadosperdidosifpe.model.MainViewModel
import br.edu.ifpe.achadosperdidosifpe.model.MetodoDevolucao
import br.edu.ifpe.achadosperdidosifpe.model.Status
import br.edu.ifpe.achadosperdidosifpe.model.Tipo
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreen
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreenMid
import coil.compose.SubcomposeAsyncImage
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

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
    var metodoDevolucao by remember { mutableStateOf<MetodoDevolucao?>(MetodoDevolucao.LEVAR_AO_SETOR) }
    var perguntaVerificacao by remember { mutableStateOf("") }
    var fotoUrl by remember { mutableStateOf<String?>(null) }

    var showMapDialog by remember { mutableStateOf(false) }
    var showPhotoOptions by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isSalvando by remember { mutableStateOf(false) }

    var nomeError by remember { mutableStateOf<String?>(null) }
    var categoriaError by remember { mutableStateOf<String?>(null) }
    var localizacaoError by remember { mutableStateOf<String?>(null) }
    var dataError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val datePickerState = rememberDatePickerState()

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
    ) { uri -> uri?.let { fotoUrl = it.toString() } }

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
                Toast.makeText(context, "Erro ao salvar foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val categorias = listOf("Documentos", "Eletrônicos", "Acessórios", "Vestuário", "Material escolar", "Outros")
    val scrollState = rememberScrollState()

    fun validarCampos(): Boolean {
        var isValid = true
        if (nome.isBlank()) { nomeError = "Campo obrigatório."; isValid = false } else nomeError = null
        if (categoriaSelecionada.isBlank()) { categoriaError = "Selecione uma categoria."; isValid = false } else categoriaError = null
        if (localizacao.isBlank()) { localizacaoError = "Informe onde perdeu."; isValid = false } else localizacaoError = null
        if (data.isBlank()) { dataError = "Informe a data."; isValid = false } else dataError = null
        return isValid
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
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = IfpeGreen)
            }
            Text("Perdi um item", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(185.dp)
                    .clickable { if (!isSalvando) showPhotoOptions = true },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.5.dp, Color(0xFFBBBBBB)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (fotoUrl != null) {
                        SubcomposeAsyncImage(
                            model = fotoUrl,
                            contentDescription = "Foto selecionada",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            loading = {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = IfpeGreen, modifier = Modifier.size(28.dp))
                                }
                            },
                            error = {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.BrokenImage, contentDescription = "Erro na foto", tint = Color.Gray)
                                }
                            }
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(36.dp))
                            Text("Clique para adicionar foto do item", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }
            }

            FormField(label = "O que você perdeu? *") {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it; if (nomeError != null) nomeError = null },
                    placeholder = { Text("Ex: Fone de ouvido Bluetooth", color = Color.LightGray) },
                    isError = nomeError != null,
                    supportingText = { nomeError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true,
                    enabled = !isSalvando
                )
            }

            FormField(label = "Categoria *") {
                ExposedDropdownMenuBox(
                    expanded = categoriaExpanded,
                    onExpandedChange = { if (!isSalvando) categoriaExpanded = it }
                ) {
                    OutlinedTextField(
                        value = categoriaSelecionada,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Selecione...", color = Color.LightGray) },
                        isError = categoriaError != null,
                        supportingText = { categoriaError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
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
                                    categoriaError = null
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
                    placeholder = { Text("Ex: Preto", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true,
                    enabled = !isSalvando
                )
            }

            FormField(label = "Onde perdeu? *", icon = Icons.Default.LocationOn) {
                OutlinedTextField(
                    value = localizacao,
                    onValueChange = { localizacao = it; if (localizacaoError != null) localizacaoError = null },
                    placeholder = { Text("Selecionar no mapa ou digitar...", color = Color.LightGray) },
                    isError = localizacaoError != null,
                    supportingText = { localizacaoError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    trailingIcon = {
                        IconButton(
                            enabled = !isSalvando,
                            onClick = {
                                if (!hasLocationPermission) {
                                    permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                                } else {
                                    showMapDialog = true
                                }
                            }
                        ) {
                            Icon(Icons.Default.Map, contentDescription = "Abrir mapa", tint = IfpeGreenMid)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true,
                    enabled = !isSalvando
                )
            }

            FormField(label = "Quando perdeu? *", icon = Icons.Default.CalendarMonth) {
                OutlinedTextField(
                    value = data,
                    onValueChange = { data = it; if (dataError != null) dataError = null },
                    placeholder = { Text("dd/mm/aaaa", color = Color.LightGray) },
                    isError = dataError != null,
                    supportingText = { dataError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }, enabled = !isSalvando) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Abrir calendário", tint = IfpeGreenMid)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true,
                    enabled = !isSalvando
                )
            }

            FormField(label = "Preferência de Devolução", icon = Icons.Default.Handshake) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = metodoDevolucao == MetodoDevolucao.LEVAR_AO_SETOR,
                        onClick = { metodoDevolucao = MetodoDevolucao.LEVAR_AO_SETOR },
                        label = { Text("Via Setor de Achados", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = IfpeGreen, selectedLabelColor = Color.White)
                    )
                    FilterChip(
                        selected = metodoDevolucao == MetodoDevolucao.DEVOLVER_PESSOALMENTE,
                        onClick = { metodoDevolucao = MetodoDevolucao.DEVOLVER_PESSOALMENTE },
                        label = { Text("Direto Comigo", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = IfpeGreen, selectedLabelColor = Color.White)
                    )
                }
            }

            FormField(label = "Pergunta para quem encontrar (opcional)", icon = Icons.Default.HelpOutline) {
                OutlinedTextField(
                    value = perguntaVerificacao,
                    onValueChange = { perguntaVerificacao = it },
                    placeholder = { Text("Ex: Qual o adesivo no verso?", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true,
                    enabled = !isSalvando
                )
            }

            FormField(label = "Características Únicas", icon = Icons.Default.Fingerprint) {
                OutlinedTextField(
                    value = caracteristicas,
                    onValueChange = { caracteristicas = it },
                    placeholder = { Text("Ex: Tem uma capinha vermelha arranhada.", color = Color.LightGray) },
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
                    placeholder = { Text("Qualquer informação extra...", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    maxLines = 3,
                    enabled = !isSalvando
                )
            }

            Button(
                onClick = {
                    if (!validarCampos()) {
                        Toast.makeText(context, "Preencha os campos obrigatórios (*)", Toast.LENGTH_SHORT).show()
                    } else {
                        isSalvando = true
                        val parsedDate = try {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(data) ?: Date()
                        } catch (e: Exception) { Date() }

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
                            metodoDevolucao = metodoDevolucao,
                            perguntaVerificacao = perguntaVerificacao.ifBlank { null },
                            fotoUrl = null,
                            data = parsedDate
                        )
                        viewModel.addItemComFoto(context, item, fotoUrl) { sucesso ->
                            isSalvando = false
                            if (sucesso) {
                                Toast.makeText(context, "Item perdido registrado!", Toast.LENGTH_SHORT).show()
                                onNavigateToItems()
                            } else {
                                Toast.makeText(context, "Falha ao enviar dados.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                enabled = !isSalvando,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IfpeGreen)
            ) {
                if (isSalvando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrar item perdido", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }
                        data = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.time)
                        if (dataError != null) dataError = null
                    }
                    showDatePicker = false
                }) { Text("Confirmar", color = IfpeGreen) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar", color = Color.Gray) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showPhotoOptions) {
        AlertDialog(
            onDismissRequest = { showPhotoOptions = false },
            title = { Text("Adicionar Foto") },
            text = { Text("Escolha de onde capturar a foto do item.") },
            confirmButton = { TextButton(onClick = { showPhotoOptions = false; cameraLauncher.launch(null) }) { Text("Câmera", color = IfpeGreen) } },
            dismissButton = { TextButton(onClick = { showPhotoOptions = false; galleryLauncher.launch("image/*") }) { Text("Galeria", color = IfpeGreen) } }
        )
    }

    if (showMapDialog) {
        Dialog(onDismissRequest = { showMapDialog = false }) {
            Card(modifier = Modifier.fillMaxWidth().height(450.dp), shape = RoundedCornerShape(16.dp)) {
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
                                if (localizacaoError != null) localizacaoError = null
                                showMapDialog = false
                            }
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showMapDialog = false }) { Text("Cancelar", color = IfpeGreen) }
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
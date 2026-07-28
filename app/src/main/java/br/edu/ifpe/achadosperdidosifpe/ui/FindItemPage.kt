package br.edu.ifpe.achadosperdidosifpe.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import br.edu.ifpe.achadosperdidosifpe.model.Item
import br.edu.ifpe.achadosperdidosifpe.model.MainViewModel
import br.edu.ifpe.achadosperdidosifpe.model.MetodoDevolucao
import br.edu.ifpe.achadosperdidosifpe.model.Status
import br.edu.ifpe.achadosperdidosifpe.model.Tipo
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreen
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreenMid
import br.edu.ifpe.achadosperdidosifpe.ui.theme.fieldColors
import coil.compose.SubcomposeAsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
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
fun FindItemPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToItems: () -> Unit = {}
) {
    var nome by remember { mutableStateOf("") }
    var categoriaExpanded by remember { mutableStateOf(false) }
    var categoriaSelecionada by remember { mutableStateOf("") }
    var corPrincipal by remember { mutableStateOf("") }
    var setor by remember { mutableStateOf<String?>(null) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
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
                val file = File(context.cacheDir, "item_${System.currentTimeMillis()}.jpg")
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
        val temLocalizacao = !setor.isNullOrBlank() || (latitude != null && longitude != null)
        if (!temLocalizacao) {
            localizacaoError = "Informe onde encontrou/perdeu o item.";
            isValid = false
        } else {
            localizacaoError = null
        }
        if (data.isBlank()) { dataError = "Informe a data."; isValid = false } else dataError = null
        return isValid
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
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
            Text("Encontrei um item", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        }

        HorizontalDivider(color = Color(0xFFE2E8F0))

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
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
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
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = IfpeGreenMid, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Clique para adicionar foto do item", fontSize = 13.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            FormField(label = "O que você encontrou? *") {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it; if (nomeError != null) nomeError = null },
                    placeholder = { Text("Ex: Caderno de Cálculo", color = Color(0xFF94A3B8)) },
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
                        placeholder = { Text("Selecione...", color = Color(0xFF94A3B8)) },
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
                                text = { Text(categoria, color = Color(0xFF0F172A)) },
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
                    placeholder = { Text("Ex: Azul escuro", color = Color(0xFF94A3B8)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true,
                    enabled = !isSalvando
                )
            }

            FormField(label = "Onde encontrou? *", icon = Icons.Default.LocationOn) {
                val localizacaoTextoExibicao = when {
                    !setor.isNullOrBlank() -> setor!!
                    latitude != null && longitude != null -> "Ponto no mapa"
                    else -> ""
                }
                LocationFieldWithMapPreview(
                    localizacao = localizacaoTextoExibicao,
                    latitude = latitude,
                    longitude = longitude,
                    isError = localizacaoError != null,
                    errorMessage = localizacaoError,
                    enabled = !isSalvando,
                    onOpenMapDialog = {
                        if (!hasLocationPermission) {
                            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        } else {
                            showMapDialog = true
                        }
                    },
                    onOpenGoogleMaps = { abrirNoGoogleMaps(context, setor, latitude, longitude) },
                    onClearLocation = {
                        setor = null
                        latitude = null
                        longitude = null
                        localizacaoError = null
                    }
                )
            }

            FormField(label = "Quando encontrou? *", icon = Icons.Default.CalendarMonth) {
                OutlinedTextField(
                    value = data,
                    onValueChange = { data = it; if (dataError != null) dataError = null },
                    placeholder = { Text("dd/mm/aaaa", color = Color(0xFF94A3B8)) },
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

            FormField(label = "Método de Devolução", icon = Icons.Default.Handshake) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = metodoDevolucao == MetodoDevolucao.LEVAR_AO_SETOR,
                        onClick = { metodoDevolucao = MetodoDevolucao.LEVAR_AO_SETOR },
                        label = { Text("Entregar no Setor", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = IfpeGreen, selectedLabelColor = Color.White)
                    )
                    FilterChip(
                        selected = metodoDevolucao == MetodoDevolucao.DEVOLVER_PESSOALMENTE,
                        onClick = { metodoDevolucao = MetodoDevolucao.DEVOLVER_PESSOALMENTE },
                        label = { Text("Devolver Pessoalmente", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = IfpeGreen, selectedLabelColor = Color.White)
                    )
                }
            }

            FormField(label = "Pergunta de Verificação (opcional)", icon = Icons.Default.HelpOutline) {
                OutlinedTextField(
                    value = perguntaVerificacao,
                    onValueChange = { perguntaVerificacao = it },
                    placeholder = { Text("Ex: Qual adesivo está colado na capa?", color = Color(0xFF94A3B8)) },
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
                    placeholder = { Text("Ex: Possui risco vermelho no lado esquerdo...", color = Color(0xFF94A3B8)) },
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
                    placeholder = { Text("Qualquer outra informação relevante...", color = Color(0xFF94A3B8)) },
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
                            tipo = Tipo.ENCONTRADO,
                            status = Status.NO_SETOR,
                            nome = nome,
                            categoria = categoriaSelecionada,
                            corPrincipal = corPrincipal.ifBlank { null },
                            setor = setor,
                            latitude = latitude,
                            longitude = longitude,
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
                                Toast.makeText(context, "Item encontrado registrado!", Toast.LENGTH_SHORT).show()
                                onNavigateToItems()
                            } else {
                                Toast.makeText(context, "Falha ao enviar os dados.", Toast.LENGTH_LONG).show()
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
                    Text("Registrar item encontrado", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
            title = { Text("Adicionar Foto", fontWeight = FontWeight.Bold) },
            text = { Text("Escolha a origem da foto do item.") },
            confirmButton = { TextButton(onClick = { showPhotoOptions = false; cameraLauncher.launch(null) }) { Text("Câmera", color = IfpeGreen, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { showPhotoOptions = false; galleryLauncher.launch("image/*") }) { Text("Galeria", color = IfpeGreen, fontWeight = FontWeight.Bold) } }
        )
    }

    if (showMapDialog) {
        LocationSelectionDialog(
            initialLocation = setor ?: "",
            hasLocationPermission = hasLocationPermission,
            onRequestPermission = {
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            },
            onDismissRequest = { showMapDialog = false },
            onLocationSelected = { selSetor, selLat, selLng ->
                setor = selSetor
                latitude = selLat
                longitude = selLng
                localizacaoError = null
                showMapDialog = false
            }
        )
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

@Composable
private fun LocationFieldWithMapPreview(
    localizacao: String,
    latitude: Double?,
    longitude: Double?,
    isError: Boolean,
    errorMessage: String?,
    enabled: Boolean,
    onOpenMapDialog: () -> Unit,
    onOpenGoogleMaps: () -> Unit,
    onClearLocation: () -> Unit
) {
    val latLng = remember(latitude, longitude) {
        if (latitude != null && longitude != null) {
            LatLng(latitude, longitude)
        } else null
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (localizacao.isNotBlank()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, if (isError) MaterialTheme.colorScheme.error else Color(0xFFCBD5E1)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    if (latLng != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clickable { onOpenGoogleMaps() }
                        ) {
                            val cameraPosState = rememberCameraPositionState {
                                position = CameraPosition.fromLatLngZoom(latLng, 16f)
                            }
                            val markerState = rememberMarkerState(position = latLng)

                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPosState,
                                uiSettings = MapUiSettings(
                                    zoomControlsEnabled = false,
                                    scrollGesturesEnabled = false,
                                    zoomGesturesEnabled = false,
                                    tiltGesturesEnabled = false,
                                    rotationGesturesEnabled = false,
                                    myLocationButtonEnabled = false
                                )
                            ) {
                                Marker(
                                    state = markerState,
                                    title = "Local Selecionado"
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .align(Alignment.TopEnd),
                                color = Color.Black.copy(alpha = 0.65f),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OpenInNew,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Abrir no Maps",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = IfpeGreenMid,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = localizacao,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0F172A),
                                maxLines = 2
                            )
                        }

                        Row {
                            IconButton(onClick = onOpenMapDialog, enabled = enabled) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Alterar local",
                                    tint = IfpeGreen
                                )
                            }
                            IconButton(onClick = onClearLocation, enabled = enabled) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remover local",
                                    tint = Color(0xFFDC2626)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Clique no ícone de mapa para escolher...", color = Color(0xFF94A3B8)) },
                isError = isError,
                supportingText = { errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                trailingIcon = {
                    IconButton(onClick = onOpenMapDialog, enabled = enabled) {
                        Icon(Icons.Default.Map, contentDescription = "Abrir mapa", tint = IfpeGreenMid)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = enabled) { onOpenMapDialog() },
                shape = RoundedCornerShape(10.dp),
                colors = fieldColors(),
                singleLine = true,
                enabled = enabled
            )
        }

        if (errorMessage != null && localizacao.isNotBlank()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun FormField(
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
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            )
        }
        content()
    }
}
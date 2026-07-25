package br.edu.ifpe.achadosperdidosifpe.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.achadosperdidosifpe.model.Status
import br.edu.ifpe.achadosperdidosifpe.model.Tipo
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreen
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreenMid

data class ItemFilter(
    val categoria: String? = null,
    val tipo: Tipo? = null,
    val status: Status? = null,
    val cor: String? = null
) {
    val isActive: Boolean get() = categoria != null || tipo != null || status != null || !cor.isNullOrBlank()
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    currentFilter: ItemFilter,
    onDismissRequest: () -> Unit,
    onApplyFilter: (ItemFilter) -> Unit
) {
    var selectedCategoria by remember { mutableStateOf(currentFilter.categoria) }
    var selectedTipo by remember { mutableStateOf(currentFilter.tipo) }
    var selectedStatus by remember { mutableStateOf(currentFilter.status) }
    var corInput by remember { mutableStateOf(currentFilter.cor ?: "") }

    val categorias = listOf("Documentos", "Eletrônicos", "Acessórios", "Vestuário", "Material escolar", "Outros")

    // Cores de alto contraste para acessibilidade visual
    val sectionHeaderColor = Color(0xFF111827)
    val chipUnselectedBg = Color(0xFFF3F4F6)
    val chipUnselectedBorder = Color(0xFFD1D5DB)
    val chipUnselectedText = Color(0xFF1F2937)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros Avançados",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = sectionHeaderColor
                )
                TextButton(onClick = {
                    selectedCategoria = null
                    selectedTipo = null
                    selectedStatus = null
                    corInput = ""
                }) {
                    Text("Limpar", color = Color(0xFFD32F2F), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)

            // Categoria
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Categoria",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = sectionHeaderColor
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categorias.forEach { cat ->
                        val isSelected = selectedCategoria == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCategoria = if (selectedCategoria == cat) null else cat
                            },
                            label = {
                                Text(
                                    text = cat,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IfpeGreen,
                                selectedLabelColor = Color.White,
                                containerColor = chipUnselectedBg,
                                labelColor = chipUnselectedText
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) IfpeGreen else chipUnselectedBorder,
                                selectedBorderColor = IfpeGreen
                            )
                        )
                    }
                }
            }

            // Tipo de Item
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Tipo de Item",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = sectionHeaderColor
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val perdidosSelected = selectedTipo == Tipo.PERDIDO
                    FilterChip(
                        selected = perdidosSelected,
                        onClick = { selectedTipo = if (selectedTipo == Tipo.PERDIDO) null else Tipo.PERDIDO },
                        label = {
                            Text(
                                text = "Perdido",
                                fontWeight = if (perdidosSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFC5221F),
                            selectedLabelColor = Color.White,
                            containerColor = chipUnselectedBg,
                            labelColor = chipUnselectedText
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = perdidosSelected,
                            borderColor = if (perdidosSelected) Color(0xFFC5221F) else chipUnselectedBorder
                        )
                    )

                    val encontradosSelected = selectedTipo == Tipo.ENCONTRADO
                    FilterChip(
                        selected = encontradosSelected,
                        onClick = { selectedTipo = if (selectedTipo == Tipo.ENCONTRADO) null else Tipo.ENCONTRADO },
                        label = {
                            Text(
                                text = "Encontrado",
                                fontWeight = if (encontradosSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IfpeGreen,
                            selectedLabelColor = Color.White,
                            containerColor = chipUnselectedBg,
                            labelColor = chipUnselectedText
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = encontradosSelected,
                            borderColor = if (encontradosSelected) IfpeGreen else chipUnselectedBorder
                        )
                    )
                }
            }

            // Status
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Status",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = sectionHeaderColor
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Status.entries.forEach { status ->
                        val label = when (status) {
                            Status.NO_SETOR -> "No Setor"
                            Status.PERDIDO -> "Em Busca"
                            Status.RESOLVIDO -> "Resolvido"
                        }
                        val isSelected = selectedStatus == status
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedStatus = if (selectedStatus == status) null else status },
                            label = {
                                Text(
                                    text = label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IfpeGreenMid,
                                selectedLabelColor = Color.White,
                                containerColor = chipUnselectedBg,
                                labelColor = chipUnselectedText
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) IfpeGreenMid else chipUnselectedBorder
                            )
                        )
                    }
                }
            }

            // Cor Principal
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Cor Principal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = sectionHeaderColor
                )
                OutlinedTextField(
                    value = corInput,
                    onValueChange = { corInput = it },
                    placeholder = { Text("Ex: Azul, Preto...", color = Color(0xFF6B7280)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IfpeGreen,
                        unfocusedBorderColor = chipUnselectedBorder,
                        focusedLabelColor = IfpeGreen,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Botão Aplicar Filtros com Alto Contraste
            Button(
                onClick = {
                    onApplyFilter(
                        ItemFilter(
                            categoria = selectedCategoria,
                            tipo = selectedTipo,
                            status = selectedStatus,
                            cor = corInput.ifBlank { null }
                        )
                    )
                    onDismissRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IfpeGreen,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "Aplicar Filtros",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
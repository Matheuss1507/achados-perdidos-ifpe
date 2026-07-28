package br.edu.ifpe.achadosperdidosifpe.ui.theme

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val IfpeGreen = Color(0xFF00642F)
val IfpeGreenMid = Color(0xFF00913F)

@Composable
fun fieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFF0F172A),
    unfocusedTextColor = Color(0xFF0F172A),
    focusedBorderColor = IfpeGreen,
    unfocusedBorderColor = Color(0xFFCBD5E1),
    focusedLabelColor = IfpeGreen,
    unfocusedLabelColor = Color(0xFF64748B),
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White
)
package br.edu.ifpe.achadosperdidosifpe.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route
    @Serializable
    data object Items : Route
    @Serializable
    data object Chat : Route
    @Serializable
    data object Profile : Route

    @Serializable
    data class ItemDetails(val itemId: String) : Route


    @Serializable
    data object ReportLostItem : Route

    @Serializable
    data object FindItem : Route
}

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: Route
) {
    data object HomeButton :
        BottomNavItem("Início", Icons.Default.Home, Route.Home)
    data object ItemsButton :
        BottomNavItem("Itens", Icons.AutoMirrored.Filled.ListAlt, Route.Items)
    data object ChatButton :
        BottomNavItem("Chat", Icons.Default.ChatBubbleOutline, Route.Chat)
    data object ProfileButton :
        BottomNavItem("Perfil", Icons.Default.Person, Route.Profile)
}
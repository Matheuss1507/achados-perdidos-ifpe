package br.edu.ifpe.achadosperdidosifpe.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.edu.ifpe.achadosperdidosifpe.ui.HomePage
import br.edu.ifpe.achadosperdidosifpe.ui.ChatPage
import br.edu.ifpe.achadosperdidosifpe.ui.ItemsPage
import br.edu.ifpe.achadosperdidosifpe.ui.ProfilePage

@Composable
fun MainNavHost(navController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(navController = navController, startDestination = Route.Home) {
        composable<Route.Home> { HomePage(modifier = modifier) }
        composable<Route.Items> { ItemsPage(modifier = modifier) }
        composable<Route.Chat> { ChatPage(modifier = modifier) }
        composable<Route.Profile> { ProfilePage(modifier = modifier) }
    }
}
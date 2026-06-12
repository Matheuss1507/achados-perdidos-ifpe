package br.edu.ifpe.achadosperdidosifpe.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import br.edu.ifpe.achadosperdidosifpe.LoginScreen
import br.edu.ifpe.achadosperdidosifpe.ui.HomePage
import br.edu.ifpe.achadosperdidosifpe.ui.ChatPage
import br.edu.ifpe.achadosperdidosifpe.ui.FindItemPage
import br.edu.ifpe.achadosperdidosifpe.ui.HomePage
import br.edu.ifpe.achadosperdidosifpe.ui.ItemsPage
import br.edu.ifpe.achadosperdidosifpe.ui.ProfilePage
import br.edu.ifpe.achadosperdidosifpe.ui.ItemDetailsPage
import br.edu.ifpe.achadosperdidosifpe.ui.RegisterPage
import br.edu.ifpe.achadosperdidosifpe.ui.itens
import br.edu.ifpe.achadosperdidosifpe.ui.ReportLostItemPage

@Composable
fun MainNavHost(navController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(navController = navController, startDestination = Route.Home) {
        composable<Route.Home> {
            HomePage(
                modifier = modifier,
                onReportLostItem = { navController.navigate(Route.ReportLostItem) },
                onFindItem = { navController.navigate(Route.FindItem) },
                onItemClick = { itemId ->
                    navController.navigate(Route.ItemDetails(itemId))
                }
            )
        }

        composable<Route.Items> { ItemsPage(modifier = modifier) }
        composable<Route.Chat> { ChatPage(modifier = modifier) }
        composable<Route.Profile> { ProfilePage(modifier = modifier) }

        composable<Route.ReportLostItem> {
            ReportLostItemPage(
                modifier = modifier,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Route.FindItem> {
            FindItemPage(
                modifier = modifier,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Route.ItemDetails> { backStackEntry ->
            val routeArgs = backStackEntry.toRoute<Route.ItemDetails>()

            val itemEncontrado = itens.find { it.id == routeArgs.itemId }

            ItemDetailsPage(
                item = itemEncontrado,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<Route.Register> {
            RegisterPage(
                modifier = modifier,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Route.Login> {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Route.Register) }
            )
        }
    }
}
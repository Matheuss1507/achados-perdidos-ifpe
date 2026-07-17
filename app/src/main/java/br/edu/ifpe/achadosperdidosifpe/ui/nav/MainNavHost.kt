package br.edu.ifpe.achadosperdidosifpe.ui.nav

import android.app.Activity import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import br.edu.ifpe.achadosperdidosifpe.LoginActivity
import br.edu.ifpe.achadosperdidosifpe.LoginScreen
import br.edu.ifpe.achadosperdidosifpe.model.MainViewModel
import br.edu.ifpe.achadosperdidosifpe.ui.HomePage
import br.edu.ifpe.achadosperdidosifpe.ui.ChatPage
import br.edu.ifpe.achadosperdidosifpe.ui.FindItemPage
import br.edu.ifpe.achadosperdidosifpe.ui.ItemsPage
import br.edu.ifpe.achadosperdidosifpe.ui.ProfilePage
import br.edu.ifpe.achadosperdidosifpe.ui.ItemDetailsPage
import br.edu.ifpe.achadosperdidosifpe.ui.RegisterPage
import br.edu.ifpe.achadosperdidosifpe.ui.LostItemPage

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    initialScreen: String? = null,
    viewModel: MainViewModel
) {
    LaunchedEffect(initialScreen) {
        if (initialScreen == "register") {
            navController.navigate(Route.Register)
        }
    }
    NavHost(navController = navController, startDestination = Route.Home) {
        composable<Route.Home> {
            HomePage(
                modifier = modifier,
                viewModel = viewModel,
                onLostItem = { navController.navigate(Route.LostItem) },
                onFindItem = { navController.navigate(Route.FindItem) },
                onItemClick = { itemId -> navController.navigate(Route.ItemDetails(itemId)) },
                onSeeAllClick = { navController.navigate(Route.Items) }
            )
        }
        composable<Route.Items> {
            ItemsPage(
                modifier = modifier,
                viewModel = viewModel,
                onItemClick = { itemId -> navController.navigate(Route.ItemDetails(itemId)) }
            )
        }
        composable<Route.Chat> { ChatPage(modifier = modifier) }
        composable<Route.Profile> {
            val context = LocalContext.current
            ProfilePage(
                modifier = modifier,
                onLogoutClick = {
                    val intent = Intent(context, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                    (context as? Activity)?.finish()
                }
            )
        }

        composable<Route.LostItem> {
            LostItemPage(
                modifier = modifier,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToItems = {
                    navController.navigate(Route.Items) {
                        popUpTo(Route.Home)
                    }
                }
            )
        }

        composable<Route.FindItem> {
            FindItemPage(
                modifier = modifier,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToItems = {
                    navController.navigate(Route.Items) {
                        popUpTo(Route.Home)
                    }
                }
            )
        }

        composable<Route.ItemDetails> { backStackEntry ->
            val routeArgs = backStackEntry.toRoute<Route.ItemDetails>()
            val item = viewModel.items.find { it.id == routeArgs.itemId }
            ItemDetailsPage(
                item = item,
                onBackClick = { navController.popBackStack() },
                onChatClick = { navController.navigate(Route.Chat) }
            )
        }

        composable<Route.Register> {
            val context = LocalContext.current
            RegisterPage(
                modifier = modifier,
                onNavigateBack = {
                    if (initialScreen == "register") {
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                        (context as? Activity)?.finish()
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }
        composable<Route.Login> {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Route.Register) }
            )
        }
    }
}
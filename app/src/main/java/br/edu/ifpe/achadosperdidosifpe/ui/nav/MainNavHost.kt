package br.edu.ifpe.achadosperdidosifpe.ui.nav

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    initialScreen: String? = null,
    viewModel: MainViewModel
) {
    // Define a rota inicial dinamicamente antes de montar a árvore de telas
    val startDestination: Route = remember(initialScreen) {
        if (initialScreen == "register") Route.Register else Route.Home
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Route.Home> {
            HomePage(
                viewModel = viewModel,
                onLostItem = { navController.navigate(Route.LostItem) },
                onFindItem = { navController.navigate(Route.FindItem) },
                onItemClick = { itemId -> navController.navigate(Route.ItemDetails(itemId)) },
                onSeeAllClick = { navController.navigate(Route.Items) }
            )
        }
        composable<Route.Items> {
            ItemsPage(
                viewModel = viewModel,
                onItemClick = { itemId -> navController.navigate(Route.ItemDetails(itemId)) }
            )
        }
        composable<Route.Chat> {
            ChatPage(
                viewModel = viewModel
            )
        }
        composable<Route.Profile> {
            val context = LocalContext.current
            ProfilePage(
                viewModel = viewModel,
                onLogoutClick = {
                    Firebase.auth.signOut()
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
                viewModel = viewModel,
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
                viewModel = viewModel,
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
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onChatClick = { chatId ->
                    viewModel.setChatInicial(chatId)
                    navController.navigate(Route.Chat)
                }
            )
        }

        composable<Route.ChatComId> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.ChatComId>()
            ChatPage(
                viewModel = viewModel,
                chatIdInicial = route.chatId
            )
        }
        composable<Route.Register> {
            val context = LocalContext.current
            RegisterPage(
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
package br.edu.ifpe.achadosperdidosifpe

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.edu.ifpe.achadosperdidosifpe.db.fb.DatabaseProvider
import br.edu.ifpe.achadosperdidosifpe.model.MainViewModel
import br.edu.ifpe.achadosperdidosifpe.model.MainViewModelFactory
import br.edu.ifpe.achadosperdidosifpe.ui.nav.BottomNavBar
import br.edu.ifpe.achadosperdidosifpe.ui.nav.BottomNavItem
import br.edu.ifpe.achadosperdidosifpe.ui.nav.MainNavHost
import br.edu.ifpe.achadosperdidosifpe.ui.nav.Route
import br.edu.ifpe.achadosperdidosifpe.ui.theme.AchadosPerdidosIFPETheme

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory(DatabaseProvider.database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mainViewModel.setContext(this)

        val openScreen = intent?.getStringExtra("OPEN_SCREEN")
        val itemIdExtra = intent?.getStringExtra("ITEM_ID")

        setContent {
            val context = LocalContext.current
            val navController = rememberNavController()

            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { }

            LaunchedEffect(Unit) {
                mainViewModel.startListeningChats()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                    if (!hasPermission) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            DisposableEffect(Unit) {
                val listener = Consumer<Intent> { newIntent ->
                    val screen = newIntent.getStringExtra("OPEN_SCREEN")
                    val itemId = newIntent.getStringExtra("ITEM_ID")
                    if (screen == "details" && !itemId.isNullOrBlank()) {
                        navController.navigate(Route.ItemDetails(itemId))
                    }
                }
                addOnNewIntentListener(listener)
                onDispose { removeOnNewIntentListener(listener) }
            }

            LaunchedEffect(openScreen, itemIdExtra) {
                if (openScreen == "details" && !itemIdExtra.isNullOrBlank()) {
                    navController.navigate(Route.ItemDetails(itemIdExtra))
                }
            }

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val items = listOf(
                BottomNavItem.HomeButton,
                BottomNavItem.ItemsButton,
                BottomNavItem.ChatButton,
                BottomNavItem.ProfileButton
            )
            val showBottomBar = items.any { item ->
                currentDestination?.hasRoute(item.route::class) == true
            }

            AchadosPerdidosIFPETheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(navController = navController, items = items)
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        MainNavHost(
                            navController = navController,
                            initialScreen = openScreen,
                            viewModel = mainViewModel
                        )
                    }
                }
            }
        }
    }
}
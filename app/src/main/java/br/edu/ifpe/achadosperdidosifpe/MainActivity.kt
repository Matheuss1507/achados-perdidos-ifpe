package br.edu.ifpe.achadosperdidosifpe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.edu.ifpe.achadosperdidosifpe.db.fb.DatabaseProvider
import br.edu.ifpe.achadosperdidosifpe.model.MainViewModel
import br.edu.ifpe.achadosperdidosifpe.model.MainViewModelFactory
import br.edu.ifpe.achadosperdidosifpe.ui.nav.BottomNavBar
import br.edu.ifpe.achadosperdidosifpe.ui.nav.BottomNavItem
import br.edu.ifpe.achadosperdidosifpe.ui.nav.MainNavHost
import br.edu.ifpe.achadosperdidosifpe.ui.theme.AchadosPerdidosIFPETheme

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory(DatabaseProvider.database)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val openScreen = intent?.getStringExtra("OPEN_SCREEN")

        setContent {
            val navController = rememberNavController()

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
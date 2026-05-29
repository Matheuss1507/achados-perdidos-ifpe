package br.edu.ifpe.achadosperdidosifpe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import br.edu.ifpe.achadosperdidosifpe.ui.nav.BottomNavBar
import br.edu.ifpe.achadosperdidosifpe.ui.nav.BottomNavItem
import br.edu.ifpe.achadosperdidosifpe.ui.nav.MainNavHost
import br.edu.ifpe.achadosperdidosifpe.ui.theme.AchadosPerdidosIFPETheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            AchadosPerdidosIFPETheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val items = listOf(
                            BottomNavItem.HomeButton,
                            BottomNavItem.ItemsButton,
                            BottomNavItem.ChatButton,
                            BottomNavItem.ProfileButton
                        )
                        BottomNavBar(navController = navController, items = items)
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        MainNavHost(navController = navController)
                    }
                }
            }
        }
    }
}
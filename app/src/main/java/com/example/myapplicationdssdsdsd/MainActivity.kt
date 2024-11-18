package com.example.myapplicationdssdsdsd

import SavedScreenUI
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplicationdssdsdsd.ui.theme.GenerateQRUI
import com.example.myapplicationdssdsdsd.ui.theme.LoginScreen
import com.example.myapplicationdssdsdsd.ui.theme.MyApplicationdssdsdsdTheme
import com.example.myapplicationdssdsdsd.ui.theme.RegistrationScreen
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Inicializar Firebase
        enableEdgeToEdge()
        setContent {
            MyApplicationdssdsdsdTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(navController = navController)
                }
            }
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable(
            route = "login?registrationSuccess={registrationSuccess}",
            arguments = listOf(
                navArgument("registrationSuccess") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val registrationSuccess = backStackEntry.arguments?.getBoolean("registrationSuccess") ?: false
            LoginScreen(navController = navController, registrationSuccess = registrationSuccess)
        }
        composable("registration") {
            RegistrationScreen(navController = navController)
        }
        composable("SavedScreenUI") {
            SavedScreenUI(navController = navController)
        }
        composable("GenerateQRUI") {
            GenerateQRUI(navController = navController)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
        style = MaterialTheme.typography.displayLarge
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationdssdsdsdTheme {
        Greeting("Android")
    }
}

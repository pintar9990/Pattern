package com.example.myapplicationdssdsdsd

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplicationdssdsdsd.ui.theme.GenerateQRUI
import com.example.myapplicationdssdsdsd.ui.theme.LoginScreen
import com.example.myapplicationdssdsdsd.ui.theme.MyApplicationdssdsdsdTheme
import com.example.myapplicationdssdsdsd.ui.theme.ProfileScreen // Este es el nuevo import directo
import com.example.myapplicationdssdsdsd.ui.theme.RegistrationScreen
import com.example.myapplicationdssdsdsd.ui.theme.SavedScreenUI
import com.google.firebase.FirebaseApp

class MainActivity : FragmentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Inicializar Firebase
        setContent {
            MyApplicationdssdsdsdTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(this, navController = navController)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(fragmentActivity: FragmentActivity, navController: NavHostController) {
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
        composable("QrScannerFragment") {
            QrScanner(fragmentActivity)
        }
        composable("ProfileScreenUI") {
            ProfileScreen(navController = navController) // Llama directamente a la función ProfileScreen
        }
    }
}

@Composable
fun QrScanner(fragmentActivity: FragmentActivity) {
    AndroidView(factory = { context ->
        FrameLayout(context).apply {
            id = View.generateViewId()
            fragmentActivity.supportFragmentManager.beginTransaction()
                .replace(this.id, QrScannerFragment())
                .commit()
        }
    })
}

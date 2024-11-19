package com.example.myapplicationdssdsdsd

import QRCodeScanner
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.example.myapplicationdssdsdsd.ui.theme.SavedScreenUI
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    private lateinit var qrCodeScanner: QRCodeScanner

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Inicializar Firebase
        qrCodeScanner = QRCodeScanner(this)
        setContent {
            MyApplicationdssdsdsdTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(navController = navController, startQRCodeScan = { startQRCodeScan() })
                }
            }
        }
    }

    private val qrCodeScanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val qrResult = qrCodeScanner.handleResult(result.resultCode, result.resultCode, result.data)
        if (qrResult != null) {
            // Maneja el resultado del código QR
        }
    }

    fun startQRCodeScan() {
        qrCodeScanner.startScan()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(navController: NavHostController, startQRCodeScan: () -> Unit) {
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
        composable("QRCodeScanner") {
            QRCodeScannerScreen(startQRCodeScan = startQRCodeScan)
        }
    }
}

@Composable
fun QRCodeScannerScreen(startQRCodeScan: () -> Unit) {
    // Aquí puedes agregar la UI para iniciar el escaneo
    // Por ejemplo, un botón que llame a startQRCodeScan
    startQRCodeScan()
}

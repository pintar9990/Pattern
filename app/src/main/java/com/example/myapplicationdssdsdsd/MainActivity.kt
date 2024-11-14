package com.example.myapplicationdssdsdsd

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
import com.example.myapplicationdssdsdsd.ui.theme.LoginScreen
import com.example.myapplicationdssdsdsd.ui.theme.MyApplicationdssdsdsdTheme
import com.example.myapplicationdssdsdsd.ui.theme.RegistrationScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationdssdsdsdTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen()
                }
            }
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

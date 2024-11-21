package com.example.myapplicationdssdsdsd
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ToolBox(
    navController: NavController
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x33D9D9D9))
                .graphicsLayer(alpha = 1f)
                .padding(vertical = 15.dp, horizontal = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.recent_icon),
                contentDescription = "Navigation Icon 1",
                modifier = Modifier
                    .size(38.dp)
                    .clickable { navController.navigate("QrScannerFragment") }
            )
            Image(
                painter = painterResource(id = R.drawable.message_icon),
                contentDescription = "Navigation Icon 2",
                modifier = Modifier
                    .size(35.dp)
                    .clickable { navController.navigate("messageScreen") }
            )
            Image(
                painter = painterResource(id = R.drawable.scan_icon),
                contentDescription = "Scan or generate QR",
                modifier = Modifier
                    .size(45.dp)
                    .clickable { navController.navigate("GenerateQRUI") }
            )
            Image(
                painter = painterResource(id = R.drawable.folder_s_icon),
                contentDescription = "Saved QR",
                modifier = Modifier
                    .size(36.dp)
                    .clickable { navController.navigate("SavedScreenUI") }
            )
            Image(
                painter = painterResource(id = R.drawable.user_s_icon),
                contentDescription = "Profile Screen",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { navController.navigate("ProfileScreenUI") }
            )
        }
    }
}

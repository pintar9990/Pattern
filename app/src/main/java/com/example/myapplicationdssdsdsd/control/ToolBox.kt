package com.example.myapplicationdssdsdsd.control

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplicationdssdsdsd.R


@Composable
fun ToolBox(
    navController: NavController,
    currentScreen: String,
    onScreenSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 0.dp)
            .background(Color.Transparent)
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .background(Color.White)
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x33D9D9D9))
                    .padding(vertical = 15.dp, horizontal = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                ToolBoxItem(
                    painterResource(id = R.drawable.folder_s_icon),
                    "SavedScreenUI",
                    currentScreen,
                    navController,
                    onScreenSelected
                )

                ToolBoxItem(
                    painterResource(id = R.drawable.url_icon),
                    "GenerateQRUI",
                    currentScreen,
                    navController,
                    onScreenSelected
                )

                ToolBoxItem(
                    painterResource(id = R.drawable.scan_icon),
                    "QrScannerFragment",
                    currentScreen,
                    navController,
                    onScreenSelected
                )

                ToolBoxItem(
                    painterResource(id = R.drawable.message_icon),
                    "ContactsScreen",
                    currentScreen,
                    navController,
                    onScreenSelected
                )

                ToolBoxItem(
                    painterResource(id = R.drawable.user_s_icon),
                    "ProfileScreenUI",
                    currentScreen,
                    navController,
                    onScreenSelected
                )
            }
        }
    }
}

@Composable
fun ToolBoxItem(
    painter: Painter,
    screen: String,
    currentScreen: String,
    navController: NavController,
    onScreenSelected: (String) -> Unit
) {
    val isSelected = screen == currentScreen
    val colorFilter = if (isSelected) ColorFilter.tint(Color.Black) else ColorFilter.tint(Color.Gray)

    Image(
        painter = painter,
        contentDescription = screen,
        modifier = Modifier
            .size(36.dp)
            .clickable {
                onScreenSelected(screen)
                navController.navigate(screen)
            },
        colorFilter = colorFilter
    )
}

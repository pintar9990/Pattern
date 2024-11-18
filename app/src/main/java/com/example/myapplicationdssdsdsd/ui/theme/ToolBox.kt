package com.example.myapplicationdssdsdsd.ui.theme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplicationdssdsdsd.R


@Composable
fun ToolBox(
    navController: NavHostController,
    registrationSuccess: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x33D9D9D9))
                    .padding(vertical = 15.dp, horizontal = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.recent_icon),
                    contentDescription = "Navigation Icon 1",
                    modifier = Modifier.size(38.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.message_icon),
                    contentDescription = "Navigation Icon 2",
                    modifier = Modifier.size(35.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.scan_icon),
                    contentDescription = "Navigation Icon 3",
                    modifier = Modifier.size(45.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.folder_s_icon),
                    contentDescription = "Navigation Icon 4",
                    modifier = Modifier.size(36.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.user_s_icon),
                    contentDescription = "Navigation Icon 5",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}
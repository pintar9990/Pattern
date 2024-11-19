package com.example.myapplicationdssdsdsd.ui.theme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplicationdssdsdsd.R

class ProfileScreenUI {

    @Composable
    fun ProfileScreen(navController : NavController) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF7BE2F4),
                            Color(0xFFFDFDFD)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 11.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Perfil",
                    fontSize = 48.sp,
                    fontFamily = FontFamily.Default,
                    color = Color(0xCC000000)
                )

                Image(
                    painter = painterResource(id = R.drawable.user_icon),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(top = 16.dp),
                    contentScale = ContentScale.Fit
                )

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .padding(top = 35.dp),
                    color = Color(0xFF555555)
                )

                Row(
                    modifier = Modifier
                        .padding(horizontal = 25.dp, vertical = 26.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(29.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.user_s_icon),
                            contentDescription = "Username Icon",
                            modifier = Modifier.size(58.dp)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.email_icon),
                            contentDescription = "Email Icon",
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.padding(start = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(40.dp)
                    ) {
                        Text(
                            text = "Username",
                            fontSize = 32.sp,
                            color = Color(0x99000000)
                        )
                        Text(
                            text = "user1@gmail.com",
                            fontSize = 32.sp,
                            color = Color(0x99000000)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 25.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.user_s_icon),
                        contentDescription = "Settings",
                        modifier = Modifier.size(59.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.user_s_icon),
                        contentDescription = "Center Image",
                        modifier = Modifier.width(137.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.user_s_icon),
                        contentDescription = "Additional Options",
                        modifier = Modifier.size(37.dp)
                    )
                }

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .padding(top = 49.dp),
                    color = Color(0xFF555555)
                )

                Button(
                    onClick = { /* Handle save action */ },
                    modifier = Modifier
                        .padding(top = 34.dp)
                        .width(232.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                ) {
                    Text(
                        text = "Guardar",
                        fontSize = 32.sp,
                        color = Color(0xFF555555)
                    )
                }

                Spacer(modifier = Modifier.height(69.dp))
            }
        }
        ToolBox(navController = navController)
    }

}
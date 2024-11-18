import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplicationdssdsdsd.R

@Composable
fun SavedScreenUI(navController: NavHostController, registrationSuccess: Boolean = false) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF7BE2F4), Color(0xFFFDFDFD)),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 47.dp, start = 31.dp, end = 31.dp)
        ) {
            item {
                Text(
                    text = "QR guardados",
                    fontSize = 48.sp,
                    fontFamily = FontFamily(Font(R.font.lalezar_regular)),
                    color = Color(0xCC000000)
                )
                Spacer(modifier = Modifier.height(38.dp))
            }

            items(qrItems) { item ->
                QrItem(item)
                Spacer(modifier = Modifier.height(14.dp))
            }

            item {
                Spacer(modifier = Modifier.height(58.dp))
                Image(
                    painter = painterResource(id = R.drawable.qr_icon),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}

@Composable
fun QrItem(item: QrItemData) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = item.imageResId),
            contentDescription = null,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = item.text,
            fontSize = 20.sp,
            fontFamily = FontFamily.Default,
            color = Color(0x99000000)
        )
    }
}

data class QrItemData(
    val imageResId: Int,
    val text: String
)

val qrItems = listOf(
    QrItemData(R.drawable.folder_icon, "examplefolder"),
    QrItemData(R.drawable.folder_icon, "examplefolder"),
    QrItemData(R.drawable.qr_icon, "exampleurl"),
    QrItemData(R.drawable.qr_icon, "exampleurl"),
    QrItemData(R.drawable.qr_icon, "exampleurl"),
    QrItemData(R.drawable.qr_icon, "exampleurl")
)


package com.example.myapplicationdssdsdsd.model

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplicationdssdsdsd.R


class FolderItemView(
    private val folder: FolderItemData,
    private val isDeleteMode: Boolean,
    private val isSelected: Boolean,
    private val onSelect: (Boolean) -> Unit,
    private val navController: NavHostController,
    private val onTapItem: (FolderItemData) -> Unit
) {
    @Composable
    fun Render() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (!isDeleteMode) {
                        navController.navigate("FolderView/${folder.id}")
                    }
                }
                .background(if (isSelected) Color.Red else Color.Transparent)
        ) {
            if (isDeleteMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onSelect
                )
            }
            Image(
                painter = painterResource(id = R.drawable.folder_icon),
                contentDescription = "Carpeta",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = folder.name,
                fontSize = 20.sp,
                fontFamily = FontFamily.Default,
                color = Color(0x99000000)
            )
        }
    }
}

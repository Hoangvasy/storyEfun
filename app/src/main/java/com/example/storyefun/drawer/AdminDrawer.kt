package com.example.storyefun.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyefun.data.DrawerItem

@Composable
fun AdminDrawer(
    items: List<DrawerItem>,
    onDestinationClicked: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items.forEach { item ->
            Text(
                text = item.title,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDestinationClicked(item.route) }
                    .padding(vertical = 12.dp)
            )
        }
    }
}

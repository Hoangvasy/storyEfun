package com.example.storyefun.ui.screens

import android.R
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
    var query by remember { mutableStateOf("") }
    var active by remember {mutableStateOf(false)}
    val searchHistory = listOf("abc", "def")

    SearchBar(
        query = query,
        onQueryChange = {query = it},
        onSearch = { newQuery ->
            println("Performing search on query: $newQuery")
        },
        active = active,
        onActiveChange = { active = it },
        placeholder = {
            Text(text="Search...")
        },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
        },
        trailingIcon = if(active) {
            {
                IconButton(onClick = {if (query.isNotEmpty()) query ="" else active = false}) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                }
            }
        } else null
    ) {
        searchHistory.takeLast(5).forEach { item ->
            ListItem(
                modifier = Modifier.clickable{ query = item },
                headlineContent = {Text(text=item)},
                leadingContent = {
                    Icon(painter = painterResource(R.drawable.ic_menu_recent_history), contentDescription = null)
                }
            )
        }
    }
}
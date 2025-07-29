package com.example.dsaguider

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dsaguider.data.Wish
import com.example.dsaguider.data.WishViewModel
import androidx.compose.material3.ExperimentalMaterial3Api // Needed for SwipeToDismissBoxState

@OptIn(ExperimentalMaterial3Api::class) // Needed for rememberSwipeToDismissBoxState
@Composable
fun HomeView(
    navController: NavController,
    viewModel: WishViewModel
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            AppBarView(title = "Notes") {
                Toast.makeText(context, "Menu Button Clicked (Example)", Toast.LENGTH_SHORT).show()
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(all = 20.dp),
                onClick = {
                    navController.navigate(route = Screen.AddScreen.route + "/0") // Removed ${} for 0
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new wish" // Consider stringResource(R.string.add_new_wish_desc)
                )
            }
        }
    ) { paddingValues ->
        val wishlistState = viewModel.getAllWishes.collectAsState(initial = listOf())
        val wishlist = wishlistState.value

        if (wishlist.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Apply scaffold padding
                    .padding(16.dp), // Additional padding for the message
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No wishes yet. Tap '+' to add one!", // Consider stringResource
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Apply scaffold padding
                    .padding(horizontal = 8.dp) // Only horizontal, vertical added by SwipeToDismissBox
            ) {
                items(
                    items = wishlist, // Use the collected value
                    key = { wish -> wish.id }
                ) { wish ->
                    val currentWish by rememberUpdatedState(wish)

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            when (dismissValue) {
                                SwipeToDismissBoxValue.EndToStart -> {
                                    viewModel.deleteWish(currentWish)
                                    true // Confirm dismiss
                                }
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    // Handle other direction if needed, e.g., archive
                                    false // Snap back
                                }
                                SwipeToDismissBoxValue.Settled -> false // No change
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        modifier = Modifier.padding(vertical = 4.dp), // Space between items
                        enableDismissFromStartToEnd = true,
                        enableDismissFromEndToStart = true,
                        backgroundContent = {
                            val color by animateColorAsState(
                                targetValue = when (dismissState.targetValue) {
                                    SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.7f)
                                    SwipeToDismissBoxValue.StartToEnd -> Color.Green.copy(alpha = 0.7f)
                                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                                }, label = "background_color_animation"
                            )
                            val alignment = when (dismissState.targetValue) {
                                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                else -> Alignment.Center
                            }
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = alignment
                            ) {
                                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Icon", // Consider stringResource
                                        tint = Color.White
                                    )
                                } else if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit Icon", // Consider stringResource
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    ) {
                        // **THIS IS WHERE THE CALL IS**
                        // If WishItem is defined in this file (com.example.dsaguider.HomeView.kt)
                        // and there are no typos, this call is syntactically correct.
                        // The error "Unresolved reference: WishItem" would then be a build/cache issue.
                        WishItem(wish = wish) {
                            val id = wish.id
                            navController.navigate(Screen.AddScreen.route + "/$id")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WishItem(wish: Wish, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // Or use MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = wish.title, fontWeight = FontWeight.ExtraBold)
            Text(text = wish.description, color = Color.Cyan)
        }
    }
}
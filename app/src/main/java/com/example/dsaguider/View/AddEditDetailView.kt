package com.example.dsaguider.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.dsaguider.AppBarView
import com.example.dsaguider.R
import com.example.dsaguider.data.Wish
import com.example.dsaguider.data.WishViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDetailView(
    id: Long,
    viewModel: WishViewModel = viewModel(),
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackMessage = remember { mutableStateOf("") }

    if (id != 0L) {
        val wish = viewModel.getWishById(id).collectAsState(initial = null).value
        LaunchedEffect(wish) {
            if (wish != null) {
                viewModel.wishTitleState = wish.title
                viewModel.quantityState = wish.quantity.toString()
                viewModel.ratingState = wish.rating
                viewModel.remarksState = wish.remarks
                wish.imageUri?.let { viewModel.imageUriState = Uri.parse(it) }
            }
        }
    } else {
        // Reset states for a new item
        viewModel.resetStates()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppBarView(
                title = if (id != 0L) stringResource(id = R.string.update_item) else stringResource(id = R.string.add_item)
            ) { navController.navigateUp() }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = Color(0xFF90CAF9),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Item Details", style = MaterialTheme.typography.titleLarge)

                    // Photo Section
                    Text("PHOTOS/EVIDENCES")
                    PhotoPickerSection(imageUri = viewModel.imageUriState) { uri ->
                        viewModel.imageUriState = uri
                    }

                    // Item Name
                    WishTextField(
                        label = "Item Name",
                        value = viewModel.wishTitleState,
                        onValueChange = { viewModel.onWishTitleChanged(it) }
                    )

                    // Quantity
                    WishTextField(
                        label = "Quantity",
                        value = viewModel.quantityState,
                        onValueChange = { viewModel.onQuantityChanged(it) },
                        keyboardType = KeyboardType.Number
                    )

                    // Rating Stars
                    RatingStars(rating = viewModel.ratingState) { newRating ->
                        viewModel.onRatingChanged(newRating)
                    }

                    // Remarks
                    WishTextField(
                        label = "Remarks",
                        value = viewModel.remarksState,
                        onValueChange = { viewModel.onRemarksChanged(it) },
                        modifier = Modifier.height(100.dp),
                        maxLines = 4
                    )

                    // Save Button
                    Button(
                        onClick = {
                            if (viewModel.wishTitleState.isNotEmpty()) {
                                val newWish = Wish(
                                    id = if (id != 0L) id else 0L,
                                    title = viewModel.wishTitleState.trim(),
                                    // Remove the `description` field from here as it's not in the data class
                                    imageUri = viewModel.imageUriState?.toString(),
                                    quantity = viewModel.quantityState.toIntOrNull() ?: 0,
                                    rating = viewModel.ratingState,
                                    remarks = viewModel.remarksState
                                )
                                if (id != 0L) {
                                    viewModel.updateWish(newWish)
                                    snackMessage.value = "Item Updated"
                                } else {
                                    viewModel.addWish(newWish)
                                    snackMessage.value = "Item Added"
                                }
                                scope.launch {
                                    snackbarHostState.showSnackbar(snackMessage.value)
                                    navController.navigateUp()
                                }
                            }
                             else {
                                snackMessage.value = "Please enter an item name"
                                scope.launch {
                                    snackbarHostState.showSnackbar(snackMessage.value)
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = if (id != 0L) stringResource(id = R.string.update_item) else stringResource(id = R.string.add_item),
                            style = TextStyle(fontSize = 18.sp)
                        )
                    }
                }
            }
        }
    }
}

// Reusable composable for the photo picker section
@Composable
fun PhotoPickerSection(imageUri: Uri?, onImageSelected: (Uri?) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> onImageSelected(uri) }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            Text("Add Photo", textAlign = TextAlign.Center)
        }
        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}


// Reusable composable for the rating stars
@Composable
fun RatingStars(rating: Int, onRatingChanged: (Int) -> Unit) {
    Text("Ratings:")
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Star $i",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onRatingChanged(i) },
                tint = if (i <= rating) Color.Yellow else Color.Gray
            )
        }
    }
}

// Reusable composable for the text fields
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        maxLines = maxLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

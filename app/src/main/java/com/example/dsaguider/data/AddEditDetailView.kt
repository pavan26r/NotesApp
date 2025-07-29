package com.example.dsaguider.data

import android.R.attr.button
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dsaguider.AppBarView
import com.example.dsaguider.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDetailView(
    id: Long,
    viewModel: WishViewModel,
    navController: NavController
) {

    val scope = rememberCoroutineScope()
    // ADD THIS
    val snackMessage = remember { // This state is for your message string
        mutableStateOf("")
    }
    val snackbarHostState = remember { SnackbarHostState() }
    if(id != 0L){
       val wish  = viewModel.getWishById(id).collectAsState(initial = Wish(0L,"",""))
        viewModel.wishTitleState = wish.value.title
        viewModel.wishDescriptionState = wish.value.description
    }
    else{
        viewModel.wishTitleState =" "
        viewModel.wishDescriptionState =" "
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
           // ADD THIS to your Scaffold
            AppBarView(
                title = if (id != 0L)
                    stringResource(id = R.string.update_wish)
                else
                    stringResource(id = R.string.add_wish)
            ){navController.navigateUp()}
        },

        // what should happn when we click app button
        //
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            // Title Field
            WishTextField(
                label = "Title",
                value = viewModel.wishTitleState,
                onValueChange = { newTitle ->
                    viewModel.onWishTitleChanged(newTitle)
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            // Description Field
            WishTextField(
                label = "Description",
                value = viewModel.wishDescriptionState,
                onValueChange = { newDescription ->
                    viewModel.onWishDescriptionChanged(newDescription)
                }
            )
            // room database
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick ={
                if(viewModel.wishTitleState.isNotEmpty() && viewModel.wishDescriptionState.isNotEmpty()){
                    if(id != 0L){
                        viewModel.updateWish(
                            Wish(
                                id = id,
                                title = viewModel.wishTitleState.trim(),
                                description = viewModel.wishDescriptionState.trim()
                            )
                        )
                        snackMessage.value = "Wish Updated"


                    }
                    else{
                        viewModel.addWish(
                            Wish(
                                title = viewModel.wishTitleState.trim(),
                                description = viewModel.wishDescriptionState.trim()
                            )
                        )
                        snackMessage.value = "Wish Added"
                    }
                }
            else{
                    snackMessage.value = "Enter a field to create a wish"
            }
                scope.launch{
                    snackbarHostState.showSnackbar(snackMessage.value)
                    navController.navigateUp()
                }



            }
            ){
                Text(text = if(id != 0L) stringResource(id = R.string.update_wish)
                else stringResource(id = R.string.add_wish),
                    style = TextStyle(fontSize = 18.sp)
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

package com.example.dsaguider.data
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsaguider.Graph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WishViewModel (
    private val wishRepository: Wishrepository = Graph.wishRepository
): ViewModel() {
    // Make sure wishTitleState is defined, often as a MutableState
    var wishTitleState by mutableStateOf("")
        set // Optional: if you only want to modify it within the ViewModel

    // Function to update the title
    fun onWishTitleChanged(newTitle: String) {
        wishTitleState = newTitle
    }

    // ... other ViewModel code, like wishDescriptionState and its update function
    var wishDescriptionState by mutableStateOf("")

    fun onWishDescriptionChanged(newDescription: String) {
        wishDescriptionState = newDescription
    }
    // late initialiser ->
    lateinit var getAllWishes: Flow<List<Wish>>
    init{
        viewModelScope.launch{
            getAllWishes = wishRepository.getWishes()
        }
    }
    fun addWish(wish: Wish){
        viewModelScope.launch(Dispatchers.IO){
            wishRepository.addWish(wish = wish)
        }
    }
    fun getWishById(id: Long): Flow<Wish> {
        return wishRepository.getWishById(id)
    }
    fun updateWish(wish: Wish){
        viewModelScope.launch(Dispatchers.IO){
            wishRepository.UpdateAWish(wish)
        }
    }
    fun deleteWish(wish: Wish){
        viewModelScope.launch(Dispatchers.IO){
            wishRepository.deleteAWish(wish)
    }
}}
package com.example.dsaguider.data

import kotlinx.coroutines.flow.Flow

class Wishrepository (private val wishDao: WishDao){



    suspend fun addWish(wish: Wish){
        wishDao.addWish(wish)
    }
    suspend fun getWishes(): Flow<List<Wish>> = wishDao.getAllWish()
    fun getWishById(id: Long): Flow<Wish> {
       return wishDao.getWishById(id)
    }
    suspend fun deleteAWish(wish:Wish){
        wishDao.deleteWish(wish)
    }
    suspend fun UpdateAWish(wish:Wish){
        wishDao.updateWish(wish)
    }
}
package com.example.dsaguider

import android.content.Context
import androidx.room.Room
import com.example.dsaguider.data.WishDataBase
import com.example.dsaguider.data.Wishrepository

object Graph {
    lateinit var database: WishDataBase


    val wishRepository by lazy{
        Wishrepository(
            wishDao = database.wishDao())}
    fun provide(context: Context){
        database = Room.databaseBuilder(context, WishDataBase::class.java, "wishlist.db").build()
    }

}
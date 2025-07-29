package com.example.dsaguider.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// giving name to the database ->
@Entity(tableName = "wish-table")
data class Wish(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "wish-title")
    val title: String = "",
    @ColumnInfo(name = "wish-desc")
    val description: String = ""
)

object DummyWish {
    val wishlist = listOf(
        Wish(
            title = "Carrier Goal ",
            description = "35 LPA Job"
        ),
        Wish(
            title = "Vision",
            description = "Build An empire"
        ),
        Wish(
            title = "Mission",
            description = "To Help Poors"
        ),
        Wish(
            title = "Wish",
            description = "An Mercedes"
        )
    )
}

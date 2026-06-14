package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_items")
data class FavoriteItem(
    @PrimaryKey val id: String, // same as Product ID
    val name: String,
    val banglaName: String,
    val price: Double,
    val unit: String,
    val iconName: String,
    val category: String
)

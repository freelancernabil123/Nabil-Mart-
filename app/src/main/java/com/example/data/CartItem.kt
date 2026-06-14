package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val id: String, // same as Product ID
    val name: String,
    val banglaName: String,
    val price: Double,
    val unit: String,
    val iconName: String,
    val quantity: Int,
    val category: String
) {
    val totalPrice: Double
        get() = price * quantity
}

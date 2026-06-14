package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class NabilMartRepository(
    private val cartDao: CartDao,
    private val favoriteDao: FavoriteDao,
    private val messageDao: MessageDao
) {
    val cartItems: Flow<List<CartItem>> = cartDao.getCartItems()
    val favoriteItems: Flow<List<FavoriteItem>> = favoriteDao.getFavoriteItems()
    val supportMessages: Flow<List<SupportMessage>> = messageDao.getAllSupportMessages()

    suspend fun sendSupportMessage(sender: String, messageText: String) {
        val newMessage = SupportMessage(
            sender = sender,
            message = messageText,
            timestamp = System.currentTimeMillis()
        )
        messageDao.insertSupportMessage(newMessage)
    }

    suspend fun clearSupportMessages() {
        messageDao.clearSupportMessages()
    }

    suspend fun markMessagesAsRead(senderOfMessages: String) {
        messageDao.markAllAsReadFrom(senderOfMessages)
    }

    suspend fun addToCart(product: Product, quantity: Int = 1) {
        val existingItem = cartDao.getCartItemById(product.id)
        if (existingItem != null) {
            val updated = existingItem.copy(quantity = existingItem.quantity + quantity)
            cartDao.updateCartItem(updated)
        } else {
            val item = CartItem(
                id = product.id,
                name = product.name,
                banglaName = product.banglaName,
                price = product.price,
                unit = product.unit,
                iconName = product.iconName,
                quantity = quantity,
                category = product.category
            )
            cartDao.insertCartItem(item)
        }
    }

    suspend fun updateCartQuantity(itemId: String, quantity: Int) {
        if (quantity <= 0) {
            cartDao.deleteCartItem(itemId)
            return
        }
        val existingItem = cartDao.getCartItemById(itemId)
        if (existingItem != null) {
            val updated = existingItem.copy(quantity = quantity)
            cartDao.updateCartItem(updated)
        }
    }

    suspend fun removeFromCart(itemId: String) {
        cartDao.deleteCartItem(itemId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    suspend fun toggleFavorite(product: Product) {
        val favoriteItemsList = favoriteDao.getFavoriteItems().firstOrNull() ?: emptyList()
        val isFav = favoriteItemsList.any { it.id == product.id }
        if (isFav) {
            favoriteDao.deleteFavorite(product.id)
        } else {
            val item = FavoriteItem(
                id = product.id,
                name = product.name,
                banglaName = product.banglaName,
                price = product.price,
                unit = product.unit,
                iconName = product.iconName,
                category = product.category
            )
            favoriteDao.insertFavorite(item)
        }
    }

    fun isProductFavorite(productId: String): Flow<Boolean> {
        return favoriteDao.isFavorite(productId)
    }
}

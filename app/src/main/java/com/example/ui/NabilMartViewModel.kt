package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CartItem
import com.example.data.FavoriteItem
import com.example.data.NabilMartRepository
import com.example.data.Product
import com.example.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.data.SupportMessage

// Simple model representing a placed order
data class Order(
    val id: String,
    val date: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val status: String, // e.g., "Pending Approval", "Processing", "Delivered"
    val address: String,
    val paymentMethod: String
)

class NabilMartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NabilMartRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = NabilMartRepository(
            database.cartDao(),
            database.favoriteDao(),
            database.messageDao()
        )
    }

    // Cart and Favorites Flows
    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteItems: StateFlow<List<FavoriteItem>> = repository.favoriteItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supportMessages: StateFlow<List<SupportMessage>> = repository.supportMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and category selections
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct = _selectedProduct.asStateFlow()

    // Dynamic Products state matching real live repository operations
    private val _products = MutableStateFlow<List<Product>>(ProductRepository.products)
    val products = _products.asStateFlow()

    // Filtered Products
    val filteredProducts: StateFlow<List<Product>> = combine(
        _products,
        _searchQuery,
        _selectedCategory
    ) { productsList, query, category ->
        productsList.filter { product ->
            val matchesSearch = product.name.contains(query, ignoreCase = true) ||
                    product.banglaName.contains(query, ignoreCase = true) ||
                    product.category.contains(query, ignoreCase = true)
            val matchesCategory = category == "All" || product.category == category
            matchesSearch && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProductRepository.products)

    // Simulating Order Placements
    private val _orderHistory = MutableStateFlow<List<Order>>(
        listOf(
            Order(
                id = "NM-98214",
                date = "12 June 2026",
                items = listOf(
                    CartItem("g1", "Rupchanda Soybean Oil", "রূপচাঁদা সয়াবিন তেল", 165.0, "1 Litre", "oil_barrel", 1, "Groceries"),
                    CartItem("d2", "Farm Fresh Chicken Eggs", "ফার্মের লাল ডিম", 145.0, "1 Dozen (১২টি)", "egg", 1, "Dairy & Eggs")
                ),
                totalAmount = 310.0,
                status = "Delivered",
                address = "Sector 4, Uttara, Dhaka - 1230",
                paymentMethod = "bKash"
            )
        )
    )
    val orderHistory = _orderHistory.asStateFlow()

    // Add to Cart Action
    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(product, quantity)
        }
    }

    // Change Cart Quantity Action
    fun updateCartQuantity(itemId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(itemId, quantity)
        }
    }

    // Delete Cart Item Action
    fun removeFromCart(itemId: String) {
        viewModelScope.launch {
            repository.removeFromCart(itemId)
        }
    }

    // Clear Cart Action
    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // Toggle Favorite Action
    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            repository.toggleFavorite(product)
        }
    }

    // Helper to check if a specific item is currently favorited
    fun isProductFavorite(productId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.isProductFavorite(productId).collect {
                onResult(it)
            }
        }
    }

    // UI filters controls
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun selectProduct(product: Product?) {
        _selectedProduct.value = product
    }

    // Place Order Logic
    fun placeOrder(
        customerName: String,
        phone: String,
        address: String,
        paymentMethod: String,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            val currentCart = cartItems.value
            if (currentCart.isEmpty()) return@launch

            val total = currentCart.sumOf { it.price * it.quantity }
            val formattedDate = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
            val orderId = "NM-${(10000..99999).random()}"

            val newOrder = Order(
                id = orderId,
                date = formattedDate,
                items = currentCart,
                totalAmount = total,
                status = "Processing",
                address = "$customerName, $phone\n$address",
                paymentMethod = paymentMethod
            )

            val updatedOrders = listOf(newOrder) + _orderHistory.value
            _orderHistory.value = updatedOrders

            // clear the active shopping cart
            repository.clearCart()

            // Trigger success callback
            onSuccess(orderId)
        }
    }

    // Dynamic Admin & Product Management operations
    fun getProductById(id: String): Product? {
        return _products.value.find { it.id == id }
    }

    fun addProduct(product: Product) {
        val currentList = _products.value.toMutableList()
        currentList.add(product)
        _products.value = currentList
    }

    fun updateProduct(product: Product) {
        val currentList = _products.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == product.id }
        if (index != -1) {
            currentList[index] = product
            _products.value = currentList
        }
    }

    fun deleteProduct(productId: String) {
        val currentList = _products.value.toMutableList()
        currentList.removeAll { it.id == productId }
        _products.value = currentList
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        val currentOrders = _orderHistory.value.map { order ->
            if (order.id == orderId) {
                order.copy(status = newStatus)
            } else {
                order
            }
        }
        _orderHistory.value = currentOrders
    }

    // Live Support Message Operations
    fun sendSupportMessage(sender: String, messageText: String) {
        viewModelScope.launch {
            repository.sendSupportMessage(sender, messageText)
        }
    }

    fun clearSupportMessages() {
        viewModelScope.launch {
            repository.clearSupportMessages()
        }
    }

    fun markSupportMessagesAsRead(senderOfMessages: String) {
        viewModelScope.launch {
            repository.markMessagesAsRead(senderOfMessages)
        }
    }

    // Secure Authenticated Session for Merchant/Admin Control Center
    private val _isMerchantAuthenticated = MutableStateFlow(false)
    val isMerchantAuthenticated = _isMerchantAuthenticated.asStateFlow()

    fun authenticateMerchant(id: String, passcode: String): Boolean {
        return if (id == "nabil07" && passcode == "nabil.1234") {
            _isMerchantAuthenticated.value = true
            true
        } else {
            false
        }
    }

    fun logoutMerchant() {
        _isMerchantAuthenticated.value = false
    }
}

class NabilMartViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NabilMartViewModel::class.java)) {
            return NabilMartViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

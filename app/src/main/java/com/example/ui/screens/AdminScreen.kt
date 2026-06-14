package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Product
import com.example.ui.NabilMartViewModel
import kotlinx.coroutines.launch
import com.example.ui.Order
import com.example.ui.components.ProductIcon
import com.example.ui.theme.NabilGoldSecondary
import com.example.ui.theme.NabilGreenLight
import com.example.ui.theme.NabilGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: NabilMartViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val orders by viewModel.orderHistory.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("orders") } // "orders" or "products"

    var showAddDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }

    // Summary calculations
    val totalRevenue = orders.filter { it.status == "Delivered" }.sumOf { it.totalAmount }
    val pendingOrders = orders.filter { it.status == "Processing" }.size

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Merchant Workroom 🛡️", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("admin_back")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.logoutMerchant()
                            onBack()
                        },
                        modifier = Modifier.testTag("admin_logout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Secure Log Out",
                            tint = Color.Red
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (activeTab == "products") {
                ExtendedFloatingActionButton(
                    text = { Text("Add Item (পণ্য যোগ)", color = Color.White) },
                    icon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White) },
                    onClick = { showAddDialog = true },
                    containerColor = NabilGreenPrimary,
                    modifier = Modifier.testTag("btn_add_product")
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 1. Dashboard Metrics Grid
            Card(
                colors = CardDefaults.cardColors(containerColor = NabilGreenLight.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricColumn(
                        label = "Revenue (লাভ)",
                        value = "৳${totalRevenue.toInt()}",
                        icon = Icons.Default.MonetizationOn,
                        color = Color(0xFF2E7D32)
                    )
                    MetricColumn(
                        label = "Pending Orders",
                        value = "$pendingOrders",
                        icon = Icons.Default.HourglassEmpty,
                        color = NabilGoldSecondary
                    )
                    MetricColumn(
                        label = "Total Items",
                        value = "${products.size}",
                        icon = Icons.Default.Inventory,
                        color = NabilGreenPrimary
                    )
                }
            }

            // 2. Tab Navigation Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TabChip(
                    text = "Orders (${orders.size})",
                    selected = activeTab == "orders",
                    icon = Icons.Default.ReceiptLong,
                    onClick = { activeTab = "orders" },
                    modifier = Modifier.weight(1f).testTag("admin_tab_orders")
                )
                TabChip(
                    text = "Products (${products.size})",
                    selected = activeTab == "products",
                    icon = Icons.Default.Category,
                    onClick = { activeTab = "products" },
                    modifier = Modifier.weight(1f).testTag("admin_tab_products")
                )
                TabChip(
                    text = "Chats 💬",
                    selected = activeTab == "support",
                    icon = Icons.Default.Chat,
                    onClick = { activeTab = "support" },
                    modifier = Modifier.weight(0.9f).testTag("admin_tab_support")
                )
            }

            // 3. Dynamic lists content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (activeTab == "orders") {
                    if (orders.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No orders placed yet.", color = Color.Gray, fontSize = 14.sp)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(orders, key = { it.id }) { order ->
                                AdminOrderCard(
                                    order = order,
                                    onUpdateStatus = { status ->
                                        viewModel.updateOrderStatus(order.id, status)
                                    }
                                )
                            }
                        }
                    }
                } else if (activeTab == "products") {
                    if (products.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No products exist in catalog. Add some!", color = Color.Gray, fontSize = 14.sp)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(products, key = { it.id }) { product ->
                                AdminProductRow(
                                    product = product,
                                    onEdit = { editingProduct = product },
                                    onDelete = { viewModel.deleteProduct(product.id) }
                                )
                            }
                        }
                    }
                } else {
                    AdminChatBlock(viewModel = viewModel)
                }
            }
        }
    }

    // Modal dialogs
    if (showAddDialog) {
        ProductFormDialog(
            title = "Add New Product 🆕",
            categories = listOf("Groceries", "Fruits & Veg", "Beverages", "Dairy & Eggs", "Personal Care", "Household"),
            onDismiss = { showAddDialog = false },
            onSave = { newProduct ->
                viewModel.addProduct(newProduct)
                showAddDialog = false
            }
        )
    }

    editingProduct?.let { productToEdit ->
        ProductFormDialog(
            title = "Edit Product Details ⚙️",
            product = productToEdit,
            categories = listOf("Groceries", "Fruits & Veg", "Beverages", "Dairy & Eggs", "Personal Care", "Household"),
            onDismiss = { editingProduct = null },
            onSave = { updatedProduct ->
                viewModel.updateProduct(updatedProduct)
                editingProduct = null
            }
        )
    }
}

@Composable
fun MetricColumn(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = color)
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun TabChip(
    text: String,
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (selected) NabilGreenPrimary else Color.Transparent,
        contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = text, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
        }
    }
}

@Composable
fun AdminOrderCard(
    order: Order,
    onUpdateStatus: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().testTag("admin_order_${order.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Order ID: ${order.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(order.date, fontSize = 11.sp, color = Color.Gray)
                }

                Surface(
                    color = when (order.status) {
                        "Delivered" -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                        "Processing" -> NabilGoldSecondary.copy(alpha = 0.15f)
                        else -> Color.Red.copy(alpha = 0.15f)
                    },
                    contentColor = when (order.status) {
                        "Delivered" -> Color(0xFF4CAF50)
                        "Processing" -> NabilGoldSecondary
                        else -> Color.Red
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = order.status,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Address Details
            Text("Customer & Delivery Address:", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
            Text(order.address, fontSize = 11.sp, modifier = Modifier.padding(bottom = 8.dp))

            // Products purchased list
            Text("Purchased Items:", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
            order.items.forEach { cartItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${cartItem.quantity}x ${cartItem.name}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text("৳${cartItem.totalPrice.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total Collected: ৳${order.totalAmount.toInt()}",
                    fontWeight = FontWeight.ExtraBold,
                    color = NabilGreenPrimary,
                    fontSize = 14.sp
                )

                // Quick update actions
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (order.status != "Processing") {
                        Button(
                            onClick = { onUpdateStatus("Processing") },
                            colors = ButtonDefaults.buttonColors(containerColor = NabilGreenLight, contentColor = NabilGreenPrimary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Process", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (order.status != "Delivered") {
                        Button(
                            onClick = { onUpdateStatus("Delivered") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.White),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Deliver", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (order.status != "Cancelled") {
                        Button(
                            onClick = { onUpdateStatus("Cancelled") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f), contentColor = Color.Red),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Cancel", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminProductRow(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().testTag("admin_item_${product.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProductIcon(
                iconName = product.iconName,
                category = product.category,
                size = 48.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.banglaName,
                    fontSize = 11.sp,
                    color = NabilGreenPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "৳${product.price.toInt()} / ${product.unit} • ${product.category}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp).testTag("btn_edit_${product.id}")) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Product", tint = NabilGreenPrimary, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp).testTag("btn_delete_${product.id}")) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Product", tint = Color.Red, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormDialog(
    title: String,
    product: Product? = null,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var banglaName by remember { mutableStateOf(product?.banglaName ?: "") }
    var priceStr by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var originalPriceStr by remember { mutableStateOf(product?.originalPrice?.toString() ?: "") }
    var selectedCategory by remember { mutableStateOf(product?.category ?: categories.first()) }
    var unit by remember { mutableStateOf(product?.unit ?: "1 Kg") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var selectedIcon by remember { mutableStateOf(product?.iconName ?: "shopping_basket") }
    var isOrganic by remember { mutableStateOf(product?.isOrganic ?: false) }
    var discountText by remember { mutableStateOf(product?.discountText ?: "") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var iconExpanded by remember { mutableStateOf(false) }

    val iconChoices = listOf("apple", "oil_barrel", "rice_bowl", "grain", "coffee", "sports_bar", "local_drink", "egg", "clean_hands", "medication", "cleaning_services", "shopping_basket")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = NabilGreenPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Product English Name") },
                            placeholder = { Text("e.g. Fresh Red Apple") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("field_name")
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = banglaName,
                            onValueChange = { banglaName = it },
                            label = { Text("Product Bangla Name (বাংলা নাম)") },
                            placeholder = { Text("যেমন: তাজা লাল আপেল") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("field_bangla_name")
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = priceStr,
                                onValueChange = { priceStr = it },
                                label = { Text("Price (৳)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("field_price")
                            )
                            OutlinedTextField(
                                value = originalPriceStr,
                                onValueChange = { originalPriceStr = it },
                                label = { Text("Original Price (৳)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("field_original_price")
                            )
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = unit,
                                onValueChange = { unit = it },
                                label = { Text("Sell Unit Pack") },
                                placeholder = { Text("e.g. 1 Kg or 500ml") },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("field_unit")
                            )
                            OutlinedTextField(
                                value = discountText,
                                onValueChange = { discountText = it },
                                label = { Text("Discount Label") },
                                placeholder = { Text("e.g. ৳15 OFF") },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("field_discount")
                            )
                        }
                    }

                    // Category Selector Dropdown
                    item {
                        ExposedDropdownMenuBox(
                            expanded = categoryExpanded,
                            onExpandedChange = { categoryExpanded = !categoryExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedCategory,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Category") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor().testTag("field_category")
                            )
                            ExposedDropdownMenu(
                                expanded = categoryExpanded,
                                onDismissRequest = { categoryExpanded = false }
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            selectedCategory = cat
                                            categoryExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Icon Selector Dropdown
                    item {
                        ExposedDropdownMenuBox(
                            expanded = iconExpanded,
                            onExpandedChange = { iconExpanded = !iconExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedIcon,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Representing Icon") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = iconExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor().testTag("field_icon")
                            )
                            ExposedDropdownMenu(
                                expanded = iconExpanded,
                                onDismissRequest = { iconExpanded = false }
                            ) {
                                iconChoices.forEach { icon ->
                                    DropdownMenuItem(
                                        text = { Text(icon) },
                                        onClick = {
                                            selectedIcon = icon
                                            iconExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Product Description") },
                            placeholder = { Text("Describe freshness, health facts or origins...") },
                            modifier = Modifier.fillMaxWidth().height(100.dp).testTag("field_desc")
                        )
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Text("Organic / Pure Nature Selection?", fontWeight = FontWeight.Medium, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Switch(
                                checked = isOrganic,
                                onCheckedChange = { isOrganic = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = NabilGreenPrimary),
                                modifier = Modifier.testTag("switch_organic")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).testTag("btn_cancel_form")
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val parsedPrice = priceStr.toDoubleOrNull() ?: 0.0
                            val parsedOriginalPrice = originalPriceStr.toDoubleOrNull()
                            if (name.isNotBlank() && banglaName.isNotBlank() && parsedPrice > 0.0) {
                                val generatedProduct = Product(
                                    id = product?.id ?: "custom_prod_${(1000..9999).random()}",
                                    name = name,
                                    banglaName = banglaName,
                                    price = parsedPrice,
                                    originalPrice = parsedOriginalPrice,
                                    category = selectedCategory,
                                    description = description.ifBlank { "High quality $name carefully inspected by NabilMart." },
                                    unit = unit,
                                    rating = product?.rating ?: 4.8,
                                    reviewCount = product?.reviewCount ?: 12,
                                    iconName = selectedIcon,
                                    isOrganic = isOrganic,
                                    discountText = discountText.ifBlank { null }
                                )
                                onSave(generatedProduct)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NabilGreenPrimary),
                        modifier = Modifier.weight(1f).testTag("btn_save_form")
                    ) {
                        Text("Save Details", color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminChatBlock(
    viewModel: NabilMartViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.supportMessages.collectAsStateWithLifecycle()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Automatically mark Customer messages as read when admin enters this chat
    LaunchedEffect(messages.size) {
        viewModel.markSupportMessagesAsRead("Customer")
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = NabilGreenPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "You are replying as Nabil07 Store Merchant (মার্চেন্ট রেসপন্স)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = NabilGreenPrimary
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (messages.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No customer messages yet.", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { msg ->
                        val isMerchant = msg.sender == "Merchant"
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isMerchant) Alignment.End else Alignment.Start
                        ) {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (!isMerchant) {
                                    // User Avatar representation
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(NabilGoldSecondary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                Card(
                                    shape = RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isMerchant) 16.dp else 2.dp,
                                        bottomEnd = if (isMerchant) 2.dp else 16.dp
                                    ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isMerchant) NabilGreenPrimary else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                                    modifier = Modifier.widthIn(max = 280.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = msg.message,
                                            color = if (isMerchant) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }

                            // Message Info line
                            val timeFormat = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                            val formattedTime = timeFormat.format(java.util.Date(msg.timestamp))
                            Text(
                                text = "${if (isMerchant) "Merchant" else "Client"} • $formattedTime",
                                fontSize = 9.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("উত্তর লিখুন (Type reply as Admin...)") },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NabilGreenPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("admin_chat_reply_text"),
                maxLines = 3,
                singleLine = false
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendSupportMessage("Merchant", inputText.trim())
                        inputText = ""
                        coroutineScope.launch {
                            if (messages.isNotEmpty()) {
                                listState.animateScrollToItem(messages.size - 1)
                            }
                        }
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = NabilGreenPrimary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .size(44.dp)
                    .testTag("admin_btn_send_chat")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

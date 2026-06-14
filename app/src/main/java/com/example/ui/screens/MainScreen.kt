package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.NabilMartViewModel

enum class MainTabs(
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "tab_home"),
    FAVORITES("Wishlist", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder, "tab_favorites"),
    CART("Cart", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart, "tab_cart"),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person, "tab_profile")
}

@Composable
fun MainScreen(
    viewModel: NabilMartViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(MainTabs.HOME) }
    var isAdminOpen by remember { mutableStateOf(false) }
    var isChatOpen by remember { mutableStateOf(false) }
    var showAdminLoginDialog by remember { mutableStateOf(false) }
    
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val totalCartCount = cartItems.sumOf { it.quantity }

    val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("app_navigation_bar"),
                tonalElevation = 8.dp
            ) {
                MainTabs.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    val activeIcon = if (isSelected) tab.selectedIcon else tab.unselectedIcon

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            if (tab == MainTabs.CART && totalCartCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = Color.Red,
                                            contentColor = Color.White,
                                            modifier = Modifier.testTag("cart_badge_qty")
                                        ) {
                                            Text(totalCartCount.toString(), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = activeIcon,
                                        contentDescription = tab.title
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = activeIcon,
                                    contentDescription = tab.title
                                )
                            }
                        },
                        label = { Text(tab.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main tabs Switch Layout
            Crossfade(
                targetState = currentTab,
                animationSpec = tween(durationMillis = 250),
                label = "MainTabsNavigationCrossfade"
            ) { tab ->
                when (tab) {
                    MainTabs.HOME -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToDetails = { product ->
                                viewModel.selectProduct(product)
                            }
                        )
                    }
                    MainTabs.FAVORITES -> {
                        FavoritesScreen(
                            viewModel = viewModel,
                            onNavigateToDetails = { productId ->
                                val origProduct = viewModel.getProductById(productId)
                                if (origProduct != null) {
                                    viewModel.selectProduct(origProduct)
                                }
                            },
                            onExploreProducts = {
                                currentTab = MainTabs.HOME
                            }
                        )
                    }
                    MainTabs.CART -> {
                        CartScreen(
                            viewModel = viewModel,
                            onExploreProducts = {
                                currentTab = MainTabs.HOME
                            }
                        )
                    }
                    MainTabs.PROFILE -> {
                        val isMerchantAuthed by viewModel.isMerchantAuthenticated.collectAsStateWithLifecycle()
                        ProfileScreen(
                            viewModel = viewModel,
                            onNavigateToAdmin = {
                                if (isMerchantAuthed) {
                                    isAdminOpen = true
                                } else {
                                    showAdminLoginDialog = true
                                }
                            },
                            onNavigateToChat = { isChatOpen = true }
                        )
                    }
                }
            }

            // Real-time full-screen workspace for store administration (অ্যাডমিন কাজের প্যানেল)
            AnimatedVisibility(
                visible = isAdminOpen,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                AdminScreen(
                    viewModel = viewModel,
                    onBack = { isAdminOpen = false }
                )
            }

            // Real-time customer care chat overlay
            AnimatedVisibility(
                visible = isChatOpen,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                ChatSupportScreen(
                    viewModel = viewModel,
                    onBack = { isChatOpen = false }
                )
            }

            // Beautiful slide-up details overlay matching the modern UX stack
            AnimatedVisibility(
                visible = selectedProduct != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                selectedProduct?.let { product ->
                    ProductDetailsScreen(
                        product = product,
                        viewModel = viewModel,
                        onBack = { viewModel.selectProduct(null) }
                    )
                }
            }
        }
    }

    if (showAdminLoginDialog) {
        AdminLoginDialog(
            viewModel = viewModel,
            onDismiss = { showAdminLoginDialog = false },
            onAuthenticated = {
                showAdminLoginDialog = false
                isAdminOpen = true
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginDialog(
    viewModel: NabilMartViewModel,
    onDismiss: () -> Unit,
    onAuthenticated: () -> Unit
) {
    var adminId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = com.example.ui.theme.NabilGreenPrimary
                )
                Text(
                    text = "Verify Store Manager 🛡️",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "আইডি এবং পাসওয়ার্ড দিয়ে মার্চেন্ট প্যানেলে প্রবেশ করুন (Security Verification required).",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                OutlinedTextField(
                    value = adminId,
                    onValueChange = { 
                        adminId = it
                        errorMessage = null
                    },
                    label = { Text("Admin ID (আইডি)") },
                    placeholder = { Text("আইডি লিখুন") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = com.example.ui.theme.NabilGreenPrimary,
                        focusedLabelColor = com.example.ui.theme.NabilGreenPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("admin_login_id")
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Passcode (পাসওয়ার্ড)") },
                    placeholder = { Text("পাসওয়ার্ড লিখুন") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide passcode" else "Show passcode"
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = com.example.ui.theme.NabilGreenPrimary,
                        focusedLabelColor = com.example.ui.theme.NabilGreenPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("admin_login_password")
                )

                errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp).testTag("admin_login_error")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (viewModel.authenticateMerchant(adminId, password)) {
                        onAuthenticated()
                    } else {
                        errorMessage = "ভুল আইডি বা পাসওয়ার্ড! আবার সঠিক তথ্য দিন।"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.NabilGreenPrimary),
                modifier = Modifier.testTag("admin_login_confirm")
            ) {
                Text("Verify (লগইন)", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("admin_login_cancel")
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}


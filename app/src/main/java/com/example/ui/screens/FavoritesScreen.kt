package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.FavoriteItem
import com.example.data.ProductRepository
import com.example.ui.NabilMartViewModel
import com.example.ui.components.ProductIcon
import com.example.ui.theme.NabilGreenLight
import com.example.ui.theme.NabilGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: NabilMartViewModel,
    onNavigateToDetails: (String) -> Unit,
    onExploreProducts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteItems by viewModel.favoriteItems.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Wishlist ❤️", fontWeight = FontWeight.Bold) }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (favoriteItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(NabilGreenLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Empty Wishlist",
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "পছন্দের তালিকাটি খালি! 📝",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Mark items with a heart while browsing to keep track of fresh foods, snacks and items you love.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onExploreProducts,
                        colors = ButtonDefaults.buttonColors(containerColor = NabilGreenPrimary),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.testTag("explore_products_fav")
                    ) {
                        Text("Add Favorites (পণ্য যোগ করুন)", color = Color.White)
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                items(favoriteItems, key = { it.id }) { item ->
                    FavoriteListItemRow(
                        item = item,
                        onRowClick = { onNavigateToDetails(item.id) },
                        onRemove = {
                            val originalProduct = viewModel.getProductById(item.id)
                            if (originalProduct != null) {
                                viewModel.toggleFavorite(originalProduct)
                            }
                        },
                        onAddToCart = {
                            val originalProduct = viewModel.getProductById(item.id)
                            if (originalProduct != null) {
                                viewModel.addToCart(originalProduct, 1)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteListItemRow(
    item: FavoriteItem,
    onRowClick: () -> Unit,
    onRemove: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRowClick() }
            .testTag("fav_item_row_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // circular icon representation
            ProductIcon(
                iconName = item.iconName,
                category = item.category,
                size = 56.dp
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Text titles
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.banglaName,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "৳${item.price.toInt()} / ${item.unit}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Quick Actions: Remove or Add-to-cart
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Remove button
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("fav_remove_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove Favorite",
                        tint = Color.Red.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Rapid add-to-cart green badge
                IconButton(
                    onClick = onAddToCart,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = NabilGreenLight,
                        contentColor = NabilGreenPrimary
                    ),
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("fav_to_cart_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart quick option",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

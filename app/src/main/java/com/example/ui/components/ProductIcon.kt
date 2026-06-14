package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ProductIcon(
    iconName: String,
    category: String,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    // Elegant color gradients according to e-commerce category
    val gradientColors = when (category) {
        "Fruits & Veg" -> listOf(Color(0xFFFF7E5F), Color(0xFFFEB47B)) // Peach Orange
        "Groceries" -> listOf(Color(0xFF11998E), Color(0xFF38EF7D)) // Emerald Green
        "Beverages" -> listOf(Color(0xFF00C6FF), Color(0xFF0072FF)) // Electric Blue
        "Dairy & Eggs" -> listOf(Color(0xFFFDC830), Color(0xFFF37335)) // Sunny Gold
        "Personal Care" -> listOf(Color(0xFFED4264), Color(0xFFFFEDBC)) // Rose Tint
        else -> listOf(Color(0xFF757F9A), Color(0xFFD7DDE8)) // Cool Slate
    }

    val iconVector = when (iconName) {
        "apple" -> Icons.Default.Eco
        "eco" -> Icons.Default.Eco
        "oil_barrel" -> Icons.Default.WaterDrop // Golden liquid representation
        "rice_bowl" -> Icons.Default.RiceBowl
        "grain" -> Icons.Default.Grain
        "coffee" -> Icons.Default.LocalCafe
        "sports_bar" -> Icons.Default.SportsBar
        "local_drink" -> Icons.Default.LocalDrink
        "egg" -> Icons.Default.Egg
        "clean_hands" -> Icons.Default.CleanHands
        "medication" -> Icons.Default.Medication
        "cleaning_services" -> Icons.Default.CleaningServices
        else -> Icons.Default.ShoppingBasket
    }

    val iconColor = when (iconName) {
        "oil_barrel" -> Color(0xFFFFD700) // Golden oil
        else -> Color.White
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = iconVector,
            contentDescription = null, // decorative backdrop
            tint = iconColor,
            modifier = Modifier.padding(size / 4)
        )
    }
}

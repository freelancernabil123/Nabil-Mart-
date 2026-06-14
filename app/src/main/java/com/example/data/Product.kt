package com.example.data

data class Product(
    val id: String,
    val name: String,
    val banglaName: String,
    val price: Double,
    val originalPrice: Double? = null,
    val category: String,
    val description: String,
    val unit: String,
    val rating: Double,
    val reviewCount: Int,
    val iconName: String, // visual icon representing the product
    val isOrganic: Boolean = false,
    val discountText: String? = null
)

object ProductRepository {
    val categories = listOf(
        CategoryItem("Groceries", "নিত্যপ্রয়োজনীয়", "shopping_basket"),
        CategoryItem("Fruits & Veg", "ফল ও সবজি", "eco"),
        CategoryItem("Beverages", "পানীয়", "local_drink"),
        CategoryItem("Dairy & Eggs", "ডিম ও দুগ্ধ", "egg"),
        CategoryItem("Personal Care", "প্রসাধন সামগ্রী", "face"),
        CategoryItem("Household", "গৃহস্থালী", "home")
    )

    val products = listOf(
        // Groceries
        Product(
            id = "g1",
            name = "Rupchanda Soybean Oil",
            banglaName = "রূপচাঁদা সয়াবিন তেল",
            price = 165.0,
            originalPrice = 180.0,
            category = "Groceries",
            description = "100% pure premium quality soybean oil refined with high automated technology, perfect for daily healthy cooking needs across your family.",
            unit = "1 Litre",
            rating = 4.8,
            reviewCount = 145,
            iconName = "oil_barrel",
            discountText = "৳15 OFF"
        ),
        Product(
            id = "g2",
            name = "Premium Miniket Rice",
            banglaName = "মিনিকেট চাল প্রিমিয়াম",
            price = 360.0, // adjusted for pack
            originalPrice = 390.0,
            category = "Groceries",
            description = "High quality polished white thin grain Miniket rice, fully organic, clean, and delicious. Ideal for hosting or absolute best daily meals.",
            unit = "5 Kg Pack",
            rating = 4.7,
            reviewCount = 210,
            iconName = "rice_bowl"
        ),
        Product(
            id = "g3",
            name = "Premium Masoor Dal",
            banglaName = "দেশী মসুর ডাল প্রিমিয়াম",
            price = 135.0,
            category = "Groceries",
            description = "Finely selected, dirt-free, high protein country-origin local Masoor lentil (ডাল). Boosts health and flavors in every family lunch.",
            unit = "1 Kg",
            rating = 4.6,
            reviewCount = 85,
            iconName = "rice_bowl"
        ),
        Product(
            id = "g4",
            name = "Teer Iodized Salt",
            banglaName = "তীর আয়োডিনযুক্ত লবণ",
            price = 42.0,
            category = "Groceries",
            description = "Pure white vacuum evaporated iodized edible salt. Ensures perfect nutrient levels and clean flavor enhancement.",
            unit = "1 Kg",
            rating = 4.9,
            reviewCount = 312,
            iconName = "grain"
        ),

        // Fruits & Veg
        Product(
            id = "f1",
            name = "Organic Fresh Apple",
            banglaName = "তাজা লাল আপেল",
            price = 240.0,
            originalPrice = 270.0,
            category = "Fruits & Veg",
            description = "Sweet, crispy, and crunchy scarlet-red apples directly imported under strictly natural agricultural inspections. Loaded with antioxidants.",
            unit = "1 Kg",
            rating = 4.5,
            reviewCount = 92,
            iconName = "apple",
            isOrganic = true,
            discountText = "11% OFF"
        ),
        Product(
            id = "f2",
            name = "Fresh Green Mango",
            banglaName = "তাজা কাঁচা আম",
            price = 95.0,
            category = "Fruits & Veg",
            description = "Directly sourced from Rajshahi orchards! Crisp, tangy sour, and perfectly rich in Vitamin C. Ideal for refreshing summer juices, pickles, or chutneys.",
            unit = "1 Kg",
            rating = 4.7,
            reviewCount = 67,
            iconName = "eco"
        ),
        Product(
            id = "f3",
            name = "Red Organic Tomatoes",
            banglaName = "দেশী পাকা টমেটো",
            price = 80.0,
            originalPrice = 100.0,
            category = "Fruits & Veg",
            description = "Lush red, pulpy, and delicious farm-grown ripe tomatoes. Freshly plucked and delivered without any chemical spray coatings.",
            unit = "1 Kg",
            rating = 4.4,
            reviewCount = 118,
            iconName = "eco",
            isOrganic = true,
            discountText = "৳20 OFF"
        ),

        // Beverages
        Product(
            id = "b1",
            name = "Ceylon Premium Gold Tea",
            banglaName = "সিলন প্রিমিয়াম চা পাতা",
            price = 145.0,
            category = "Beverages",
            description = "Rich, aromatic blended local Ceylon high-grade tea leaves. Brings excellent golden color and a heavenly energetic start to your day.",
            unit = "200g Pack",
            rating = 4.9,
            reviewCount = 410,
            iconName = "coffee"
        ),
        Product(
            id = "b2",
            name = "Coca-Cola Original",
            banglaName = "কোকা-কোলা অরিজিনাল",
            price = 70.0,
            category = "Beverages",
            description = "The classic, crisp carbonated soft drink designed to keep you chilled during lunch or celebrations with friends.",
            unit = "1.25 Litre",
            rating = 4.8,
            reviewCount = 520,
            iconName = "sports_bar"
        ),

        // Dairy & Eggs
        Product(
            id = "d1",
            name = "Aarong Fresh Milk",
            banglaName = "আড়ং তরল দুধ",
            price = 90.0,
            originalPrice = 95.0,
            category = "Dairy & Eggs",
            description = "Pasteurized fresh liquid dairy milk directly collected from local diary farmers by Aarong. Strictly monitored for purity.",
            unit = "1 Litre",
            rating = 4.8,
            reviewCount = 280,
            iconName = "local_drink",
            discountText = "৳5 OFF"
        ),
        Product(
            id = "d2",
            name = "Farm Fresh Chicken Eggs",
            banglaName = "ফার্মের লাল ডিম",
            price = 145.0,
            category = "Dairy & Eggs",
            description = "Protein-packed, hygienically sorted grade-A brown chicken eggs. Delivered in secure protective cartons.",
            unit = "1 Dozen (১২টি)",
            rating = 4.7,
            reviewCount = 340,
            iconName = "egg"
        ),

        // Personal Care
        Product(
            id = "p1",
            name = "Lux Velvet Touch Soap",
            banglaName = "লাক্স সাবান ময়েশ্চারাইজার",
            price = 85.0,
            category = "Personal Care",
            description = "Infused with modern floral fragrance oils and moisturizing silk essence. Ensures beautiful, glowing, and velvety soft skin.",
            unit = "150g Bar",
            rating = 4.6,
            reviewCount = 195,
            iconName = "clean_hands"
        ),
        Product(
            id = "p2",
            name = "Sensodyne Fresh Mint",
            banglaName = "সেনসোডাইন টুথপেস্ট",
            price = 210.0,
            category = "Personal Care",
            description = "Clinically proven desensitizing formula with continuous 24/7 protection against teeth sensitivity plus a cool peppermint refresh.",
            unit = "100g Tube",
            rating = 4.8,
            reviewCount = 224,
            iconName = "medication"
        ),

        // Household
        Product(
            id = "h1",
            name = "Vim Liquid Dishwash",
            banglaName = "ভিম লিকুইড ডিশওয়াশ",
            price = 140.0,
            category = "Household",
            description = "Power-packed lemon cleaner liquid that slices through heavy oil grease easily, leaving shiny, germ-free dishes with a fragrant lemon scent.",
            unit = "500ml Bottle",
            rating = 4.9,
            reviewCount = 380,
            iconName = "cleaning_services"
        )
    )

    fun getProductById(id: String): Product? {
        return products.find { it.id == id }
    }
}

data class CategoryItem(
    val name: String,
    val banglaName: String,
    val iconName: String
)

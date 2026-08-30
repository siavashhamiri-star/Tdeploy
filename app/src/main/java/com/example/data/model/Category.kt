package com.example.data.model

data class Category(
    val id: String,
    val name: String,
    val emoji: String,
    val iconDescription: String = ""
)

object CategoryList {
    val ALL_CATEGORIES = listOf(
        Category(id = "all", name = "همه اصناف و مشاغل", emoji = "✨"),
        Category(id = "retail", name = "سوپرمارکت و اصناف خرد", emoji = "🛒"),
        Category(id = "medical", name = "پزشکان و درمانگران", emoji = "🩺"),
        Category(id = "legal", name = "وکلا و مشاوران حقوقی", emoji = "⚖️"),
        Category(id = "heavy", name = "آهن‌فروشان و مصالح ساختمانی", emoji = "🏗️"),
        Category(id = "luxury", name = "طلا، نقره و بدلیجات", emoji = "💎"),
        Category(id = "insurance", name = "کارگزاران بیمه", emoji = "🛡️"),
        Category(id = "art", name = "نقاشان و هنرمندان", emoji = "🎨"),
        Category(id = "music", name = "موزیسین‌ها و استودیوها", emoji = "🎵"),
        Category(id = "care", name = "پرستاران و مراقبان خانگی", emoji = "👵"),
        Category(id = "tech", name = "فناوری و برنامه‌نویسی", emoji = "💻"),
        Category(id = "service", name = "خدمات فنی و تاسیسات", emoji = "🔧")
    )
}

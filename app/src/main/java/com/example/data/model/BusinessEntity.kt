package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "businesses")
data class BusinessEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val desc: String,
    val location: String,
    val contact: String,
    val rating: Float = 5.0f,
    val isVerified: Boolean = true,
    val isBookmarked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

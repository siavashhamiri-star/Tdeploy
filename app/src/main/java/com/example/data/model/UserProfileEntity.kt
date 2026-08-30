package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val userName: String = "ارزش‌آفرین توانا",
    val myReferralCode: String = "TAVANA-9452",
    val referredCount: Int = 3,
    val xpScore: Int = 150,
    val leagueRank: String = "ارزش‌آفرین سطح ۱",
    val hasAcceptedDisclaimer: Boolean = false,
    val usedReferralCodes: String = ""
)

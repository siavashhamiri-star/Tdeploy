package com.example.data.repository

import com.example.data.local.BusinessDao
import com.example.data.local.UserProfileDao
import com.example.data.model.BusinessEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

class TavanaRepository(
    private val businessDao: BusinessDao,
    private val userProfileDao: UserProfileDao
) {
    val allBusinesses: Flow<List<BusinessEntity>> = businessDao.getAllBusinesses()
    val userProfile: Flow<UserProfileEntity?> = userProfileDao.getUserProfile()

    suspend fun initializeDefaultDataIfNeeded() {
        if (businessDao.count() == 0) {
            val initialList = listOf(
                BusinessEntity(
                    id = "biz_1",
                    name = "سوپرمارکت برادران",
                    category = "retail",
                    desc = "توزیع اقلام مصرفی و محله‌محور با ارسال سریع",
                    location = "تهران، قلهک",
                    contact = "021-22000000",
                    rating = 4.9f,
                    isVerified = true
                ),
                BusinessEntity(
                    id = "biz_2",
                    name = "کلینیک تخصصی قلب و عروق",
                    category = "medical",
                    desc = "ویزیت تخصصی، مشاوره و خدمات پزشکی",
                    location = "تهران، ونک",
                    contact = "021-88000000",
                    rating = 5.0f,
                    isVerified = true
                ),
                BusinessEntity(
                    id = "biz_3",
                    name = "دفتر وکالت عدالت",
                    category = "legal",
                    desc = "مشاوره حقوقی، کیفری و دعاوی تجاری",
                    location = "تهران، میدان انقلاب",
                    contact = "021-66000000",
                    rating = 4.8f,
                    isVerified = true
                ),
                BusinessEntity(
                    id = "biz_4",
                    name = "بنکداری آهن آلات پایتخت",
                    category = "heavy",
                    desc = "تامین تیرآهن، میلگرد و پروفیل صنعتی",
                    location = "تهران، بازار آهن شادآباد",
                    contact = "021-55000000",
                    rating = 4.7f,
                    isVerified = true
                ),
                BusinessEntity(
                    id = "biz_5",
                    name = "گالری طلا و جواهر هوس",
                    category = "luxury",
                    desc = "طراحی و ساخت طلاهای لوکس و کم‌اجرت",
                    location = "تهران، بازار بزرگ",
                    contact = "021-33000000",
                    rating = 4.9f,
                    isVerified = true
                ),
                BusinessEntity(
                    id = "biz_6",
                    name = "نمایندگی بیمه عمر و تکمیلی",
                    category = "insurance",
                    desc = "مشاوره و صدور انواع بیمه‌نامه‌های تخصصی",
                    location = "سراسر کشور",
                    contact = "021-44000000",
                    rating = 4.8f,
                    isVerified = true
                ),
                BusinessEntity(
                    id = "biz_7",
                    name = "گالری نقاشی و خط نگار",
                    category = "art",
                    desc = "آثار اورجینال رنگ‌روغن، آبرنگ و خطاطی اصیل",
                    location = "آنلاین",
                    contact = "09120000000",
                    rating = 5.0f,
                    isVerified = true
                ),
                BusinessEntity(
                    id = "biz_8",
                    name = "استودیو ضبط موسیقی نوا",
                    category = "music",
                    desc = "تنظیم، میکس، مسترینگ و آموزش پیانو",
                    location = "تهران",
                    contact = "09121111111",
                    rating = 4.9f,
                    isVerified = true
                ),
                BusinessEntity(
                    id = "biz_9",
                    name = "مرکز پرستاری و مراقبت در منزل مهر",
                    category = "care",
                    desc = "اعزام مراقب سالمند، کودک و بیمار با تعهد و اخلاق حرفه‌ای",
                    location = "تهران و حومه",
                    contact = "09122222222",
                    rating = 5.0f,
                    isVerified = true
                )
            )
            businessDao.insertAll(initialList)
        }

        if (userProfileDao.getProfileDirect() == null) {
            userProfileDao.insertOrUpdate(
                UserProfileEntity(
                    id = 1,
                    userName = "شهروند ارزش‌آفرین",
                    myReferralCode = "TAVANA-9452",
                    referredCount = 3,
                    xpScore = 150,
                    leagueRank = calculateRank(150),
                    hasAcceptedDisclaimer = false
                )
            )
        }
    }

    suspend fun acceptDisclaimer() {
        val current = userProfileDao.getProfileDirect() ?: UserProfileEntity()
        userProfileDao.insertOrUpdate(current.copy(hasAcceptedDisclaimer = true))
    }

    suspend fun addBusiness(business: BusinessEntity) {
        businessDao.insertBusiness(business)
        // Award XP for creating value / introducing business
        addXp(100)
    }

    suspend fun toggleBookmark(id: String, currentStatus: Boolean) {
        businessDao.updateBookmark(id, !currentStatus)
    }

    suspend fun applyReferralCode(code: String): Result<String> {
        val trimmed = code.trim().uppercase()
        val current = userProfileDao.getProfileDirect() ?: UserProfileEntity()

        if (trimmed.isEmpty()) {
            return Result.failure(Exception("لطفاً کد معرف را وارد کنید"))
        }
        if (trimmed == current.myReferralCode.uppercase()) {
            return Result.failure(Exception("نمی‌توانید از کد معرف خودتان استفاده کنید"))
        }

        val usedList = current.usedReferralCodes.split(",").map { it.trim().uppercase() }.filter { it.isNotEmpty() }
        if (usedList.contains(trimmed)) {
            return Result.failure(Exception("این کد معرف قبلاً برای شما ثبت شده است"))
        }

        val newXp = current.xpScore + 50
        val newReferred = current.referredCount + 1
        val updatedUsed = (usedList + trimmed).joinToString(",")
        val updatedRank = calculateRank(newXp)

        userProfileDao.insertOrUpdate(
            current.copy(
                xpScore = newXp,
                referredCount = newReferred,
                usedReferralCodes = updatedUsed,
                leagueRank = updatedRank
            )
        )
        return Result.success("کد معرف $trimmed با موفقیت اعمال شد و ۵۰ امتیاز مشارکت دریافت کردید!")
    }

    suspend fun addXp(amount: Int) {
        val current = userProfileDao.getProfileDirect() ?: UserProfileEntity()
        val newXp = current.xpScore + amount
        userProfileDao.insertOrUpdate(
            current.copy(
                xpScore = newXp,
                leagueRank = calculateRank(newXp)
            )
        )
    }

    companion object {
        fun calculateRank(xp: Int): String {
            return when {
                xp < 200 -> "ارزش‌آفرین سطح ۱"
                xp < 500 -> "ارزش‌آفرین سطح ۲"
                xp < 1000 -> "پیشگام همیاری (سطح ۳)"
                xp < 2000 -> "معتمد اکوسیستم (سطح ۴)"
                else -> "شورای حکمرانی توانا (سطح طلایی)"
            }
        }

        fun getNextRankInfo(xp: Int): Pair<Int, Int> {
            // Returns (currentLevelBaseXp, nextLevelTargetXp)
            return when {
                xp < 200 -> Pair(0, 200)
                xp < 500 -> Pair(200, 500)
                xp < 1000 -> Pair(500, 1000)
                xp < 2000 -> Pair(1000, 2000)
                else -> Pair(2000, 5000)
            }
        }
    }
}

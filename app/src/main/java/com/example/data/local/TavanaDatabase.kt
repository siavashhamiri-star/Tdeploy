package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.BusinessEntity
import com.example.data.model.UserProfileEntity

@Database(entities = [BusinessEntity::class, UserProfileEntity::class], version = 1, exportSchema = false)
abstract class TavanaDatabase : RoomDatabase() {
    abstract fun businessDao(): BusinessDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: TavanaDatabase? = null

        fun getDatabase(context: Context): TavanaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TavanaDatabase::class.java,
                    "tavana_city_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

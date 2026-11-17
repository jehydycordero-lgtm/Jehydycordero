package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.AddonEntity

@Database(
    entities = [AddonEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(AddonConverters::class)
abstract class AddonDatabase : RoomDatabase() {
    abstract fun addonDao(): AddonDao

    companion object {
        @Volatile
        private var INSTANCE: AddonDatabase? = null

        fun getInstance(context: Context): AddonDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AddonDatabase::class.java,
                    "bedrock_hub_addons.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

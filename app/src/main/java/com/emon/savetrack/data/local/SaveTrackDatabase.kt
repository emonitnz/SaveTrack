package com.emon.savetrack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Room database instance for SaveTrack.
 */
@Database(entities = [Transaction::class], version = 1, exportSchema = false)
@TypeConverters(TransactionTypeConverter::class)
abstract class SaveTrackDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: SaveTrackDatabase? = null

        fun getDatabase(context: Context): SaveTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SaveTrackDatabase::class.java,
                    "savetrack_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
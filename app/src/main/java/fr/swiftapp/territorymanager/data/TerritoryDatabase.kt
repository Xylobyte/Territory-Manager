package fr.swiftapp.territorymanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Territory::class], version = 2, exportSchema = false)
abstract class TerritoryDatabase : RoomDatabase() {
    abstract fun territoryDao(): TerritoryDao

    companion object {
        @Volatile
        private var Instance: TerritoryDatabase? = null

        fun getDatabase(context: Context): TerritoryDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TerritoryDatabase::class.java, "territories_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
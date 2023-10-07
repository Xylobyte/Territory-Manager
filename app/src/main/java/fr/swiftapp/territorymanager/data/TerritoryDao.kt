package fr.swiftapp.territorymanager.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TerritoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(territory: Territory)

    @Update
    suspend fun update(territory: Territory)

    @Delete
    suspend fun delete(territory: Territory)

    @Query("SELECT * FROM territories WHERE id = :id")
    fun getById(id: Int): Flow<Territory>

    @Query("SELECT * FROM territories ORDER BY number ASC")
    fun getAll(): Flow<List<Territory>>

    @Query("SELECT * FROM territories")
    fun exportAll(): Flow<List<Territory>>

    @Query("SELECT * FROM territories WHERE isAvailable = 1 ORDER BY returnDate ASC")
    fun getAllAvailable(): Flow<List<Territory>>

    @Query("SELECT * FROM territories WHERE isAvailable = 0 ORDER BY givenDate ASC")
    fun getAllGiven(): Flow<List<Territory>>
}
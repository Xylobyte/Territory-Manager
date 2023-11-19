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

    @Query("SELECT * FROM territories WHERE isShops = :isShops ORDER BY number ASC")
    fun getAll(isShops: Int): Flow<List<Territory>>

    @Query("SELECT * FROM territories")
    fun exportAll(): Flow<List<Territory>>

    @Query("SELECT * FROM territories WHERE isAvailable = 1 AND isShops = :isShops ORDER BY returnDate ASC")
    fun getAllAvailable(isShops: Int): Flow<List<Territory>>

    @Query("SELECT * FROM territories WHERE isAvailable = 0 AND isShops = :isShops ORDER BY givenDate ASC")
    fun getAllGiven(isShops: Int): Flow<List<Territory>>
}
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
    @Insert(Territory::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(territory: Territory)

    @Update(Territory::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(territory: Territory)

    @Delete(Territory::class)
    suspend fun delete(territory: Territory)

    @Query("DELETE FROM territories")
    suspend fun deleteAll()

    @Query("SELECT * FROM territories WHERE id = :id")
    fun getById(id: Int): Flow<Territory>

    @Query("SELECT * FROM territories WHERE isShops = :isShops ORDER BY number ASC")
    fun getAll(isShops: Int): Flow<List<Territory>>

    @Query("SELECT * FROM territories")
    fun exportAll(): Flow<List<Territory>>

    @Insert(TerritoryChanges::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun pushChange(change: TerritoryChanges)

    @Query("SELECT * FROM territories_changes ORDER BY id ASC")
    fun getAllChanges(): Flow<List<TerritoryChanges>>

    @Query("DELETE FROM territories_changes")
    suspend fun deleteAllChanges()
}
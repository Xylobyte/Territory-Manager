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

    /* ----------------------------- CHANGES ----------------------------- */

    @Insert(TerritoryChanges::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun pushChange(change: TerritoryChanges)

    @Query("DELETE FROM territories_changes WHERE isSaved = 1")
    suspend fun deleteAllChanges()

    @Query("UPDATE territories_changes SET isSaved = 1 WHERE id = :id")
    suspend fun markChangeAsSaved(id: Int)

    @Query("SELECT * FROM territories_changes ORDER BY id DESC")
    fun getAllChanges(): Flow<List<TerritoryChanges>>

    @Query("SELECT * FROM territories_changes")
    fun exportAllChanges(): Flow<List<TerritoryChanges>>
}
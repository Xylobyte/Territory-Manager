package fr.swiftapp.territorymanager.data

import kotlinx.coroutines.flow.Flow

interface TerritoriesRepository {
    fun exportAllStream(): Flow<List<Territory>>

    fun getAllStream(isShops: Int): Flow<List<Territory>>

    fun getByIdStream(id: Int): Flow<Territory>

    suspend fun insert(territory: Territory)

    suspend fun update(territory: Territory)

    suspend fun delete(territory: Territory)

    suspend fun deleteAll()
}

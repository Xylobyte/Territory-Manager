package fr.swiftapp.territorymanager.data

import kotlinx.coroutines.flow.Flow

class OfflineTerritoriesRepository(private val territoryDao: TerritoryDao) : TerritoriesRepository {
    override fun exportAllStream(): Flow<List<Territory>> = territoryDao.exportAll()

    override fun getAllGivenStream(isShops: Int): Flow<List<Territory>> = territoryDao.getAllGiven(isShops)

    override fun getAllAvailableStream(isShops: Int): Flow<List<Territory>> = territoryDao.getAllAvailable(isShops)

    override fun getAllStream(isShops: Int): Flow<List<Territory>> = territoryDao.getAll(isShops)

    override fun getByIdStream(id: Int): Flow<Territory> = territoryDao.getById(id)

    override suspend fun insert(territory: Territory) = territoryDao.insert(territory)

    override suspend fun update(territory: Territory) = territoryDao.update(territory)

    override suspend fun delete(territory: Territory) = territoryDao.delete(territory)

    override suspend fun deleteAll() = territoryDao.deleteAll()
}
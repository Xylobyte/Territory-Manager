package fr.swiftapp.territorymanager.data

import kotlinx.coroutines.flow.Flow

class OfflineTerritoriesRepository(private val territoryDao: TerritoryDao) : TerritoriesRepository {
    override fun exportAllStream(): Flow<List<Territory>> = territoryDao.exportAll()

    override fun getAllGivenStream(): Flow<List<Territory>> = territoryDao.getAllGiven()

    override fun getAllAvailableStream(): Flow<List<Territory>> = territoryDao.getAllAvailable()

    override fun getAllStream(): Flow<List<Territory>> = territoryDao.getAll()

    override fun getByIdStream(id: Int): Flow<Territory> = territoryDao.getById(id)

    override suspend fun insert(territory: Territory) = territoryDao.insert(territory)

    override suspend fun update(territory: Territory) = territoryDao.update(territory)

    override suspend fun delete(territory: Territory) = territoryDao.delete(territory)
}
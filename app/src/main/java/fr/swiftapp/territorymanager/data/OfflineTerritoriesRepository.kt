package fr.swiftapp.territorymanager.data

import kotlinx.coroutines.flow.Flow

class OfflineTerritoriesRepository(private val territoryDao: TerritoryDao) : TerritoriesRepository {
    override fun exportAllStream(): Flow<List<Territory>> = territoryDao.exportAll()

    override fun getAllStream(isShops: Int): Flow<List<Territory>> = territoryDao.getAll(isShops)

    override fun getByIdStream(id: Int): Flow<Territory> = territoryDao.getById(id)

    override suspend fun insert(territory: Territory) = territoryDao.insert(territory)

    override suspend fun update(territory: Territory) = territoryDao.update(territory)

    override suspend fun delete(territory: Territory) = territoryDao.delete(territory)

    override suspend fun deleteAll() = territoryDao.deleteAll()

    /* ----------------------------- CHANGES ----------------------------- */

    override suspend fun pushChange(change: TerritoryChanges) = territoryDao.pushChange(change)

    override suspend fun deleteAllChanges() = territoryDao.deleteAllChanges()

    override suspend fun markChangeAsSaved(id: Int) = territoryDao.markChangeAsSaved(id)

    override fun getAllChanges(): Flow<List<TerritoryChanges>> = territoryDao.getAllChanges()

    override fun exportAllChanges(): Flow<List<TerritoryChanges>> = territoryDao.exportAllChanges()
}
package fr.swiftapp.territorymanager.data

import android.content.Context

interface AppContainer {
    val territoriesRepository: TerritoriesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val territoriesRepository: TerritoriesRepository by lazy {
        OfflineTerritoriesRepository(TerritoryDatabase.getDatabase(context).territoryDao())
    }
}
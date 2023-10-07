package fr.swiftapp.territorymanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "territories")
data class Territory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val number: Int,
    val name: String,
    val givenDate: String,
    val returnDate: String,
    val isAvailable: Boolean,
    val givenName: String
)
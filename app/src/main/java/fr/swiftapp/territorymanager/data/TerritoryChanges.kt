package fr.swiftapp.territorymanager.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "territories_changes")
data class TerritoryChanges(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "territoryId")
    val territoryId: Int,

    @ColumnInfo(name = "territoryName")
    val territoryName: String,

    @ColumnInfo(name = "beforeGivenDate")
    val beforeGivenDate: String,

    @ColumnInfo(name = "beforeReturnDate")
    val beforeReturnDate: String,

    @ColumnInfo(name = "beforeGivenName")
    val beforeGivenName: String,

    @ColumnInfo(name = "changeType")
    val changeType: String,

    @ColumnInfo(name = "afterGivenDate")
    val afterGivenDate: String,

    @ColumnInfo(name = "afterReturnDate")
    val afterReturnDate: String,

    @ColumnInfo(name = "afterGivenName")
    val afterGivenName: String,

    @ColumnInfo(name = "isSaved")
    val isSaved: Boolean,
)
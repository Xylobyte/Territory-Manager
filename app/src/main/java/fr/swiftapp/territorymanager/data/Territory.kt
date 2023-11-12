package fr.swiftapp.territorymanager.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "territories")
data class Territory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "number")
    val number: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "givenDate")
    val givenDate: String,

    @ColumnInfo(name = "returnDate")
    val returnDate: String,

    @ColumnInfo(name = "isAvailable")
    val isAvailable: Boolean,

    @ColumnInfo(name = "givenName")
    val givenName: String,

    @ColumnInfo(name = "isShops")
    val isShops: Boolean?
)
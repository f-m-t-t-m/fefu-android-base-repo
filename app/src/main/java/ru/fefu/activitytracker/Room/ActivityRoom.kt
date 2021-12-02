package ru.fefu.activitytracker.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity
data class ActivityRoom(
        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name = "activity_type") val type: Int,
        @ColumnInfo(name = "date_start") val dateStart: Long,
        @ColumnInfo(name = "date_end") val dateEnd: Long,
        @ColumnInfo(name = "distance") val distance: Double,
        @ColumnInfo(name = "coordinates") val coordinates: List<Pair<Double, Double>>,
        @ColumnInfo(name = "finished") val finished: Int
)
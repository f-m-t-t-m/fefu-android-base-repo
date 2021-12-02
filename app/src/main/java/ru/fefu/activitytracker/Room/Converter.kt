package ru.fefu.activitytracker.Room

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converter {
    @TypeConverter
    fun fromList(list: List<Pair<Double, Double>>) = Json.encodeToString(list)

    @TypeConverter
    fun toList(string: String) = Json.decodeFromString<List<Pair<Double, Double>>>(string)
}
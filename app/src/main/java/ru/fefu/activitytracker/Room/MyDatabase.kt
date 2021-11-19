package ru.fefu.activitytracker.Room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ActivityRoom::class], version = 1)
abstract class MyDatabase: RoomDatabase() {
    abstract fun activityDao(): ActivityDao
}
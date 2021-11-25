package ru.fefu.activitytracker.Room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

import androidx.room.migration.Migration

@Database(entities = [ActivityRoom::class], version = 1)
abstract class MyDatabase: RoomDatabase() {
    abstract fun activityDao(): ActivityDao
}

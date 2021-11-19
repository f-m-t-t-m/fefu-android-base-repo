package ru.fefu.activitytracker.Room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ActivityDao {

    @Query("SELECT * FROM ActivityRoom ORDER BY date_end DESC")
    fun getAll(): LiveData<List<ActivityRoom>>

    @Insert
    fun insert(activity: ActivityRoom)
}
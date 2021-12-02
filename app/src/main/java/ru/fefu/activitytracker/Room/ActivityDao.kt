package ru.fefu.activitytracker.Room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ActivityDao {
    @Query("SELECT * FROM ActivityRoom WHERE finished=1 ORDER BY date_end DESC")
    fun getAll(): LiveData<List<ActivityRoom>>

    @Query("SELECT * FROM ActivityRoom WHERE finished=0 LIMIT 1")
    fun getUnfinished(): ActivityRoom

    @Query("SELECT * FROM ActivityRoom WHERE id=:id LIMIT 1")
    fun getById(id: Int): ActivityRoom

    @Query("SELECT * FROM ActivityRoom WHERE id=:id LIMIT 1")
    fun getByIdLiveData(id: Int): LiveData<ActivityRoom>

    @Insert
    fun insert(activity: ActivityRoom): Long

    @Query("UPDATE ActivityRoom SET finished=1, date_end=:date_end WHERE id=:id")
    fun finishActivity(date_end: Long, id: Int)

    @Query("UPDATE ActivityRoom SET coordinates=:coordinates, distance=:distance  WHERE id=:id")
    fun updateCoordinates(coordinates: List<Pair<Double, Double>>, distance: Double, id: Int)

    @Query("DELETE FROM ActivityRoom")
    fun deleteAll()
}
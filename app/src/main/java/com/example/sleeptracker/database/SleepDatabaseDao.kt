package com.example.sleeptracker.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

//TODO (01) Created Annoted Interface SleepDatabaseDao
@Dao
interface SleepDatabaseDao {
    //TODO Add annoted insert() for inserting a single sleep night
    @Insert
    fun insert(night: SleepNight)
    //TODO Add Annotated update() methood for updating a SleepNight
    @Update
    fun update(night: SleepNight)
    //TODO Add Annotated get() that gets the SleepNight by key
    @Query("SELECT * from daily_sleep_quality_table WHERE nightId = :key")
    fun get(key:Long):SleepNight?
    //TODO Add annotated clear() method and query
    @Query("DELETE FROM daily_sleep_quality_table")
    fun clear()
    //TODO add annotated getTonight() method and query
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1")
    fun getTonight(): SleepNight?
    //TODO add annotated getAllNights()
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC")
    fun getAllNights(): LiveData<List<SleepNight>>
}
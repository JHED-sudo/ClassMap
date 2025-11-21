package com.example.classmap.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ScheduleDao {

    @Insert
    void insert(Schedule schedule);

    @Update
    void update(Schedule schedule);

    @Delete
    void delete(Schedule schedule);

    @Query("SELECT * FROM schedule_table ORDER BY day, time")
    List<Schedule> getAll();
}

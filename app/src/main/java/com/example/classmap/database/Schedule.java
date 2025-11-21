package com.example.classmap.database;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "schedule_table")
public class Schedule {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String subject;
    public String room;
    public String day;
    public String time;

    public Schedule(String subject, String room, String day, String time) {
        this.subject = subject;
        this.room = room;
        this.day = day;
        this.time = time;
    }
}
package org.yuezhikong.todo.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity
data class Schedule (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val start: String,
    val end: String,
    val allday: Boolean,
    var alarm: Boolean,
    val noticeTimes: String, // 存储为逗号分隔的字符串，例如 "0,1,2"
)

@Dao
interface ScheduleDao {
    @Insert
    suspend fun insert(schedule: Schedule)

    @Update
    suspend fun update(schedule: Schedule)

    @Delete
    suspend fun delete(schedule: Schedule)

    @Query("SELECT * FROM Schedule")
    suspend fun getAll(): List<Schedule>

    @Query("SELECT * FROM Schedule WHERE id = :id")
    suspend fun getById(id: Int): Schedule?
}

suspend fun saveSchedule(db: AppDatabase, name: String, allDay: Boolean, alarm: Boolean, start: String, end: String, description: String, noticeTimes: List<Int>) {
    db.scheduleDao().insert(
        schedule = Schedule(
            title = name,
            description = description,
            start = start,
            end = end,
            allday = allDay,
            alarm = alarm,
            noticeTimes = noticeTimes.joinToString(",")
        )
    )
}

suspend fun editSchedule(db: AppDatabase, schedule: Schedule) {
    db.scheduleDao().update(schedule)
}
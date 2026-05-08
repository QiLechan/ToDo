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
    val start_date: Int,
    val start_time: Int,
    val end_date: Int,
    val end_time: Int,
    val allday: Boolean,
    val alarm: Boolean,
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

    @Query("SELECT * FROM Schedule WHERE start_date >= :startDate AND start_date <= :endDate")
    suspend fun getByStartDateRange(startDate: Int, endDate: Int): List<Schedule>

    @Query("SELECT * FROM Schedule WHERE start_date = :start_date")
    suspend fun getByStartDate(start_date: Int): List<Schedule>
}

suspend fun saveSchedule(db: AppDatabase,
                         name: String,
                         allDay: Boolean,
                         alarm: Boolean,
                         start_date: Int,
                         start_time: Int,
                         end_date: Int,
                         end_time: Int,
                         description: String,
                         noticeTimes: List<Int>
) {
    db.scheduleDao().insert(
        schedule = Schedule(
            title = name,
            description = description,
            start_date = start_date,
            start_time = start_time,
            end_date = end_date,
            end_time = end_time,
            allday = allDay,
            alarm = alarm,
            noticeTimes = noticeTimes.joinToString(",")
        )
    )
}

suspend fun editSchedule(db: AppDatabase, schedule: Schedule) {
    db.scheduleDao().update(schedule)
}
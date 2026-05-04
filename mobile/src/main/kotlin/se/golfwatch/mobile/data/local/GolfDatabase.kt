package se.golfwatch.mobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CourseEntity::class], version = 1, exportSchema = false)
abstract class GolfDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
}

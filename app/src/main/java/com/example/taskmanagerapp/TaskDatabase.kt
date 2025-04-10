package com.example.taskmanagerapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            // Return existing instance if present, otherwise build a new one
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "tasks_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

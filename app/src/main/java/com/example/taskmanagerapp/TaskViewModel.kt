package com.example.taskmanagerapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    val allTasks: androidx.lifecycle.LiveData<List<Task>>

    init {
        // Initialize Room database and Repository
        val db = TaskDatabase.getDatabase(application)
        val taskDao = db.taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.allTasks   // LiveData of task list
    }

    // Add a new task to the database
    fun addTask(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    // Update an existing task in the database
    fun updateTask(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    // Delete a task from the database
    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }
}

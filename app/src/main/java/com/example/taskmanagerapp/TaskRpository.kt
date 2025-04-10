package com.example.taskmanagerapp


class TaskRepository(private val taskDao: TaskDao) {

    // LiveData list of all tasks, automatically updated when database changes
    val allTasks: androidx.lifecycle.LiveData<List<Task>> = taskDao.getAllTasks()

    // Fetch a single task by ID (returns null if not found)
    suspend fun getTaskById(id: Int): Task? {
        return taskDao.getTaskById(id)
    }

    // Insert a new task
    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    // Update an existing task
    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    // Delete a task
    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }
}
class TaskRpository {
}
package com.example.taskmanagerapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class TaskListActivity : AppCompatActivity() {
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        // Initialize ViewModel
        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(TaskViewModel::class.java)

        // Setup RecyclerView and adapter
        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(emptyList()) { task ->
            // On task item click: open TaskDetailActivity
            val intent = Intent(this, TaskDetailActivity::class.java)
            // Pass task details to detail screen
            intent.putExtra("taskId", task.id)
            intent.putExtra("title", task.title)
            intent.putExtra("description", task.description)
            intent.putExtra("dueDate", task.dueDate)
            startActivity(intent)
        }
        recyclerView.adapter = taskAdapter

        // Observe LiveData list of tasks
        viewModel.allTasks.observe(this) { taskList ->
            // Update RecyclerView when data changes
            taskAdapter.updateTasks(taskList)
        }

        // Setup bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        // Highlight "Tasks" as the selected tab
        bottomNav.selectedItemId = R.id.menu_tasks
        // Handle bottom nav item clicks
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_tasks -> {
                    // Already on Tasks screen – do nothing
                    true
                }
                R.id.menu_add -> {
                    // Switch to Add/Edit Task screen (for a new task)
                    val intent = Intent(this, AddEditTaskActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}

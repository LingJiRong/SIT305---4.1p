package com.example.taskmanagerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class TaskDetailActivity : AppCompatActivity() {
    private lateinit var viewModel: TaskViewModel
    private var currentTask: Task? = null  // will hold the task being viewed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        // Get task data from intent extras
        val taskId = intent.getIntExtra("taskId", -1)
        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val dueDateMillis = intent.getLongExtra("dueDate", 0L)
        currentTask = Task(taskId, title, description, dueDateMillis)

        // Initialize ViewModel (for deletion operation)
        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(TaskViewModel::class.java)

        // Set task details in UI
        val titleView = findViewById<TextView>(R.id.textDetailTitle)
        val descView = findViewById<TextView>(R.id.textDetailDescription)
        val dueDateView = findViewById<TextView>(R.id.textDetailDueDate)
        titleView.text = title
        descView.text = if (description.isNotEmpty()) description else getString(R.string.no_description)
        // Format due date to display
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dueDateView.text = getString(R.string.due_date_label, dateFormat.format(Date(dueDateMillis)))

        // Edit button launches AddEditTaskActivity with this task's ID
        val buttonEdit = findViewById<Button>(R.id.buttonEdit)
        buttonEdit.setOnClickListener {
            if (taskId != -1) {
                val intent = Intent(this, AddEditTaskActivity::class.java)
                intent.putExtra("taskId", taskId)
                startActivity(intent)
            }
        }
        // Delete button deletes the task and finishes this activity
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)
        buttonDelete.setOnClickListener {
            currentTask?.let { task ->
                viewModel.deleteTask(task)
                Toast.makeText(this, R.string.task_deleted, Toast.LENGTH_SHORT).show()
            }
            finish()  // close the detail screen
        }
    }
}

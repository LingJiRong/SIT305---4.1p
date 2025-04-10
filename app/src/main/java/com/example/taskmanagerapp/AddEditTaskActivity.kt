package com.example.taskmanagerapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import android.content.Intent
import java.util.*

class AddEditTaskActivity : AppCompatActivity() {
    private lateinit var viewModel: TaskViewModel
    private var editingTaskId: Int? = null  // if not null, we are in edit mode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_task)

        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(TaskViewModel::class.java)

        // UI references
        val titleInput = findViewById<EditText>(R.id.editTextTitle)
        val descInput = findViewById<EditText>(R.id.editTextDescription)
        val dueDateInput = findViewById<EditText>(R.id.editTextDueDate)
        val saveButton = findViewById<Button>(R.id.buttonSave)

        // Setup date picker for due date field
        dueDateInput.apply {
            // Make non-editable, user must pick from DatePicker
            isFocusable = false
            isClickable = true
        }
        dueDateInput.setOnClickListener {
            // Show a DatePickerDialog when due date field is clicked
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(this, { _, y, m, d ->
                // Format picked date to dd/MM/yyyy and set in input
                val pickedCalendar = Calendar.getInstance()
                pickedCalendar.set(y, m, d, 0, 0, 0)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                dueDateInput.setText(dateFormat.format(pickedCalendar.time))
            }, year, month, day).show()
        }

        // Check if an existing task ID was passed (edit mode)
        val taskId = intent.getIntExtra("taskId", -1)
        if (taskId != -1) {
            editingTaskId = taskId
            // Load task from DB to populate fields
            lifecycleScope.launch(Dispatchers.IO) {
                val task = TaskDatabase.getDatabase(applicationContext).taskDao().getTaskById(taskId)
                task?.let {
                    withContext(Dispatchers.Main) {
                        titleInput.setText(it.title)
                        descInput.setText(it.description)
                        // Format stored date to display
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        dueDateInput.setText(dateFormat.format(Date(it.dueDate)))
                    }
                }
            }
        }

        // Handle Save button click
        saveButton.setOnClickListener {
            val titleText = titleInput.text.toString().trim()
            val descText = descInput.text.toString().trim()
            val dateText = dueDateInput.text.toString().trim()

            // Validate inputs
            var valid = true
            if (titleText.isEmpty()) {
                titleInput.error = getString(R.string.error_title_required)
                valid = false
            }
            if (dateText.isEmpty()) {
                dueDateInput.error = getString(R.string.error_date_required)
                valid = false
            }

            // If inputs are filled, validate date format
            var dueDateMillis: Long? = null
            if (dateText.isNotEmpty()) {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                dateFormat.isLenient = false  // strict date parsing
                try {
                    val date = dateFormat.parse(dateText)
                    if (date != null) {
                        dueDateMillis = date.time
                    } else {
                        valid = false
                        dueDateInput.error = getString(R.string.error_date_invalid)
                    }
                } catch (e: ParseException) {
                    valid = false
                    dueDateInput.error = getString(R.string.error_date_invalid)
                }
            }

            if (!valid) {
                return@setOnClickListener  // don't proceed if validation failed
            }

            // All inputs valid – create or update task
            if (dueDateMillis == null) dueDateMillis = System.currentTimeMillis()
            if (editingTaskId != null) {
                // Update existing task
                val updatedTask = Task(id = editingTaskId!!, title = titleText, description = descText, dueDate = dueDateMillis)
                viewModel.updateTask(updatedTask)
            } else {
                // Create new task
                val newTask = Task(title = titleText, description = descText, dueDate = dueDateMillis)
                viewModel.addTask(newTask)
            }
            // Close this screen and return to previous (task list or detail)
            finish()
        }

        // Setup bottom navigation bar
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigationView)
        // Highlight "Add" tab since we are on add/edit screen
        bottomNav.selectedItemId = R.id.menu_add
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_tasks -> {
                    // Navigate back to task list
                    val intent = Intent(this, TaskListActivity::class.java)
                    // Clear top so that going back doesn't stack multiple instances
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }
                R.id.menu_add -> {
                    // Already on Add screen
                    true
                }
                else -> false
            }
        }
    }
}

package com.example.taskmanagerapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private var tasks: List<Task>,
    private val onItemClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // ViewHolder inner class to hold reference to item views
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.textTitle)
        val dueDateText: TextView = itemView.findViewById(R.id.textDueDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        // Inflate the item_task layout
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.titleText.text = task.title
        // Format the due date timestamp to dd/MM/yyyy string
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.dueDateText.text = dateFormat.format(Date(task.dueDate))
        // Set click listener
        holder.itemView.setOnClickListener { onItemClick(task) }
    }

    override fun getItemCount(): Int = tasks.size

    // Update the list of tasks and refresh the RecyclerView
    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}

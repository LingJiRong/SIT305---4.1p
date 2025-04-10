package com.example.taskmanagerapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Immediately redirect to TaskListActivity
        val intent = Intent(this, TaskListActivity::class.java)
        startActivity(intent)
        finish() // Close MainActivity so user can't come "back" to it
    }
}

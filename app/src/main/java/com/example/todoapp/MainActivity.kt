package com.example.todoapp

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.databinding.ActivityMainBinding
import com.example.todoapp.ui.TaskAdapter
import com.example.todoapp.ui.ViewModel.TaskViewViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: TaskViewViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

    }
    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter( onTaskCheckedChanged = { task ->
                viewModel.toggleTaskCompletion(task)
            },
            onTaskDeleted = { task -> viewModel.deleteTask(task)
            }
        )
        binding.tasksRecyclerView.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }
    private fun setupObservers() {
        viewModel.allTasks.observe(this) { tasks ->
            taskAdapter.submitList(tasks)
        }
    }
    private fun setupClickListeners() {
        binding.addTaskButton.setOnClickListener {
            showAddTaskDialog()
        }
    }
    private fun showAddTaskDialog(){
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Add Task")
            .setView(R.layout.add_item)
            .setPositiveButton("Add") {dialog, _ ->
                val dialogView: View? = (dialog as AlertDialog).findViewById<View>(R.id.dialogLayout)
                val titleEdit: TextInputEditText? = dialogView?.findViewById<TextInputEditText>(R.id.titleEdit)
                val descriptionEdit: TextInputEditText? = dialogView?.findViewById<TextInputEditText>(R.id.descriptionEdit)

                val title = titleEdit?.text.toString()
                val description = descriptionEdit?.text.toString()

                if (title.isNotEmpty() && description.isNotEmpty()) {
                    viewModel.addTask(title, description)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }
}
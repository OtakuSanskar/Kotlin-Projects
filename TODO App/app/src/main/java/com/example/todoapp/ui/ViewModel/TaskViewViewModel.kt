package com.example.todoapp.ui.ViewModel

import androidx.lifecycle.ViewModel
import com.example.todoapp.data.entity.Task
import com.example.todoapp.data.repository.Repository

class TaskViewViewModel: ViewModel() {
    private val repository = Repository()
    val allTasks = repository.allTasks
    private var taskCounterId = 0

    fun addTask(title: String, description: String) {
        val task = Task(taskCounterId++, title, description)
        repository.addTask(task)
    }
    fun deleteTask(task: Task) {
        repository.deleteTask(task)
    }
    fun toggleTaskCompletion(task: Task) {
        task.isCompleted = !task.isCompleted
        repository.updateTask(task)
    }
}
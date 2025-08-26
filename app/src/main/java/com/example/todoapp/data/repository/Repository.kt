package com.example.todoapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.data.entity.Task

class Repository {
    private val tasks = mutableListOf<Task>()
    private val _allTasks = MutableLiveData<List<Task>>()
    val allTasks: LiveData<List<Task>> = _allTasks

    fun addTask(task: Task) {
        tasks.add(task)
        _allTasks.value = tasks.toList()
    }

    fun deleteTask(task: Task) {
        tasks.remove(task)
        _allTasks.value = tasks.toList()
    }

    fun updateTask(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            _allTasks.value = tasks.toList()
        }
    }
}
package com.example.todoapp.data.entity

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    var isCompleted: Boolean = false
)

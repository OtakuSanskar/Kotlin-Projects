package com.example.todoapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.entity.Task
import com.example.todoapp.databinding.ItemLayoutBinding

class TaskAdapter(
    private val onTaskCheckedChanged: (Task) -> Unit,
    private val onTaskDeleted: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val binding = ItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return TaskViewHolder(binding)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        inner class TaskViewHolder(private val binding: ItemLayoutBinding) :
                RecyclerView.ViewHolder(binding.root) {

            fun bind(task: Task) {
                binding.apply {
                    taskTitle.text = task.title
                    taskDescription.text = task.description
                    taskCheckBox.isChecked = task.isCompleted

                    taskCheckBox.setOnClickListener {
                        onTaskCheckedChanged(task)
                    }

                    deleteButton.setOnClickListener {
                        onTaskDeleted(task)
                    }
                }
            }
        }

    class TaskDiffCallback: DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem == newItem
    }
}

package com.kotlin.practice.todoapp.contents.providers

import com.kotlin.practice.todoapp.contents.models.TaskData

interface TaskDataStoreInterface {
    fun fetchTaskData(): List<TaskData>?
    fun updateTaskData(taskList: List<TaskData>?)
    fun deleteTaskData(primaryKeys: List<Int>)
}

class TaskDataProvider(private val taskDataStore: TaskDataStoreInterface) {
    fun fetchTaskData(): List<TaskData>? {
        return taskDataStore.fetchTaskData()
    }

    fun updateTaskData(taskList: List<TaskData>?) {
        taskDataStore.updateTaskData(taskList)
    }

    fun deleteTaskData(primaryKeys: List<Int>) {
        taskDataStore.deleteTaskData(primaryKeys)
    }
}

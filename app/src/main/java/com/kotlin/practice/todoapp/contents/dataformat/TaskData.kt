package com.kotlin.practice.todoapp.contents.dataformat

data class TaskData(
    val primaryKey: Long,
    var task: String,
    var isChecked: Boolean
)

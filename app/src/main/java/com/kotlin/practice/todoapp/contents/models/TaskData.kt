package com.kotlin.practice.todoapp.contents.models

data class TaskData(
    val primaryKey: Long,
    var task: String,
    var isChecked: Boolean
)

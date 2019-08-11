package com.kotlin.practice.todoapp.contents.models

data class TaskData(
    val primaryKey: Int,
    var task: String,
    var isChecked: Boolean
)

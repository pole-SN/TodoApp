package com.kotlin.practice.todoapp.contents.scenes.todocontents

import com.kotlin.practice.todoapp.contents.models.TaskData

object FetchTasks {
    object FetchTaskData {
        class Request
        data class Response(
            val taskList: List<TaskData>?
        )

        data class ViewModel(
            val taskList: List<TaskData>
        )
    }
}

object UpdateTasks {
    object UpdateTaskData {
        data class Request(
            val taskList: List<TaskData>?
        )

        class Response
        class ViewModel
    }
}


object DeleteTasks {
    object DeleteTaskData {
        data class Request(
            val primaryKeys: List<Int>
        )

        class Response
        class ViewModel
    }
}

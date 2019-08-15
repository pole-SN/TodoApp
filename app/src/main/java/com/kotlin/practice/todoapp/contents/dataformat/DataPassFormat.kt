package com.kotlin.practice.todoapp.contents.dataformat

object FetchTasks {
    object FetchTaskData {
        class Request
        data class Response(
            val taskList: List<TaskData>
        )
    }
}

object UpdateTasks {
    object AddTaskData {
        data class Request(
            val task: String
        )

        data class Response(
            val taskData: TaskData
        )
    }

    object SingleUpdateTaskData {
        data class Request(
            val taskData: TaskData
        )

        data class Response(
            val taskData: TaskData
        )
    }

    object MultiUpdateTaskData {
        data class Request(
            val taskList: List<TaskData>
        )

        data class Response(
            val taskList: List<TaskData>
        )
    }
}

object DeleteTasks {
    object DeleteTaskData {
        data class Request(
            val primaryKeys: List<Long>
        )

        data class Response(
            val primaryKeys: List<Long>
        )
    }
}

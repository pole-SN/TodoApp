package com.kotlin.practice.todoapp.contents.ui

import com.kotlin.practice.todoapp.contents.dataformat.DeleteTasks
import com.kotlin.practice.todoapp.contents.dataformat.FetchTasks
import com.kotlin.practice.todoapp.contents.dataformat.TaskData
import com.kotlin.practice.todoapp.contents.dataformat.UpdateTasks
import com.kotlin.practice.todoapp.contents.usecase.BusinessLogic


interface RequestLogic {
    fun fetchTaskData()
    fun addTaskData(task: String)
    fun singleUpdateTaskData(taskData: TaskData)
    fun multiUpdateTaskData(taskList: List<TaskData>)
    fun deleteCheckedTaskData()
    fun deleteTaskData(primaryKeys: List<Long>)
}

interface PresentationLogic {
    fun presentTaskData(response: FetchTasks.FetchTaskData.Response)
    fun presentAddTaskData(response: UpdateTasks.AddTaskData.Response)
    fun presentSingleUpdateTaskData(response: UpdateTasks.SingleUpdateTaskData.Response)
    fun presentMultiUpdateTaskData(response: UpdateTasks.MultiUpdateTaskData.Response)
    fun presentDeleteTaskData(response: DeleteTasks.DeleteTaskData.Response)
}

class TodoContentsPresenter : RequestLogic, PresentationLogic {
    lateinit var mUseCase: BusinessLogic
    lateinit var mFragment: DisplayLogic

    override fun fetchTaskData() {
        val request = FetchTasks.FetchTaskData.Request()
        mUseCase.fetchTaskData(request)
    }

    override fun addTaskData(task: String) {
        val request = UpdateTasks.AddTaskData.Request(task)
        mUseCase.addTaskData(request)
    }

    override fun singleUpdateTaskData(taskData: TaskData) {
        val request =
            UpdateTasks.SingleUpdateTaskData.Request(
                taskData
            )
        mUseCase.singleUpdateTaskData(request)
    }

    override fun multiUpdateTaskData(taskList: List<TaskData>) {
        val request =
            UpdateTasks.MultiUpdateTaskData.Request(taskList)
        mUseCase.multiUpdateTaskData(request)
    }

    override fun deleteCheckedTaskData() {
        mUseCase.deleteCheckedTaskData()
    }

    override fun deleteTaskData(primaryKeys: List<Long>) {
        val request =
            DeleteTasks.DeleteTaskData.Request(primaryKeys)
        mUseCase.deleteTaskData(request)
    }

    override fun presentTaskData(response: FetchTasks.FetchTaskData.Response) {
        mFragment.displayTaskData(response)
    }

    override fun presentAddTaskData(response: UpdateTasks.AddTaskData.Response) {
        mFragment.displayAddTaskData(response)
    }

    override fun presentSingleUpdateTaskData(response: UpdateTasks.SingleUpdateTaskData.Response) {
        mFragment.displaySingleUpdateTaskData(response)
    }

    override fun presentMultiUpdateTaskData(response: UpdateTasks.MultiUpdateTaskData.Response) {
        mFragment.displayMultiUpdateTaskData(response)
    }

    override fun presentDeleteTaskData(response: DeleteTasks.DeleteTaskData.Response) {
        mFragment.displayDeleteTaskData(response)
    }
}

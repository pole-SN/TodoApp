package com.kotlin.practice.todoapp.contents.usecase

import com.kotlin.practice.todoapp.contents.dataformat.DeleteTasks
import com.kotlin.practice.todoapp.contents.dataformat.FetchTasks
import com.kotlin.practice.todoapp.contents.dataformat.UpdateTasks
import com.kotlin.practice.todoapp.contents.model.RealmRepository
import com.kotlin.practice.todoapp.contents.ui.PresentationLogic


interface BusinessLogic {
    fun fetchTaskData(request: FetchTasks.FetchTaskData.Request)
    fun addTaskData(request: UpdateTasks.AddTaskData.Request)
    fun singleUpdateTaskData(request: UpdateTasks.SingleUpdateTaskData.Request)
    fun multiUpdateTaskData(request: UpdateTasks.MultiUpdateTaskData.Request)
    fun deleteCheckedTaskData()
    fun deleteTaskData(request: DeleteTasks.DeleteTaskData.Request)
}

class TodoContentsUseCase : BusinessLogic {
    private val mRepository = RealmRepository()
    lateinit var mPresenter: PresentationLogic

    override fun fetchTaskData(request: FetchTasks.FetchTaskData.Request) {
        val taskList = mRepository.fetchTaskData()
        val response =
            FetchTasks.FetchTaskData.Response(
                taskList
            )
        mPresenter.presentTaskData(response)
    }

    override fun addTaskData(request: UpdateTasks.AddTaskData.Request) {
        val taskData = mRepository.addTaskData(request.task)
        val response =
            UpdateTasks.AddTaskData.Response(
                taskData
            )
        mPresenter.presentAddTaskData(response)
    }

    override fun singleUpdateTaskData(request: UpdateTasks.SingleUpdateTaskData.Request) {
        val taskList = mutableListOf(request.taskData)
        mRepository.updateTaskData(taskList)

        val response =
            UpdateTasks.SingleUpdateTaskData.Response(
                request.taskData
            )
        mPresenter.presentSingleUpdateTaskData(response)
    }

    override fun multiUpdateTaskData(request: UpdateTasks.MultiUpdateTaskData.Request) {
        mRepository.updateTaskData(request.taskList)
        val response =
            UpdateTasks.MultiUpdateTaskData.Response(
                request.taskList
            )
        mPresenter.presentMultiUpdateTaskData(response)
    }

    override fun deleteCheckedTaskData() {
        var primaryKeys: MutableList<Long> = mutableListOf()
        for (taskData in mRepository.fetchTaskData()) {
            if (taskData.isChecked) primaryKeys.add(taskData.primaryKey)
        }
        deleteTaskData(
            DeleteTasks.DeleteTaskData.Request(
                primaryKeys
            )
        )
    }

    override fun deleteTaskData(request: DeleteTasks.DeleteTaskData.Request) {
        mRepository.deleteTaskData(request.primaryKeys)
        val response =
            DeleteTasks.DeleteTaskData.Response(
                request.primaryKeys
            )
        mPresenter.presentDeleteTaskData(response)
    }
}

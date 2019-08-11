package com.kotlin.practice.todoapp.contents.scenes.todocontents

import com.kotlin.practice.todoapp.contents.externals.TaskDataRealmStore
import com.kotlin.practice.todoapp.contents.models.TaskData
import com.kotlin.practice.todoapp.contents.providers.TaskDataProvider

interface BusinessLogic {
    fun fetchTaskData(request: FetchTasks.FetchTaskData.Request)
    fun updateAllCheckBox(isChecked: Boolean)
    fun updateTaskData(request: UpdateTasks.UpdateTaskData.Request)
    fun deleteCheckedTaskData()
    fun deleteTaskData(request: DeleteTasks.DeleteTaskData.Request)
}

interface DataStore {
    var taskList: List<TaskData>
    var taskData: TaskData?
    var primaryKeys: List<String>
}

class TodoContentsDataInteractor : BusinessLogic, DataStore {
    lateinit var mPresenter: PresentationLogic
    private val mTaskDataProvider = TaskDataProvider(TaskDataRealmStore())
    override var taskList: List<TaskData> = mTaskDataProvider.fetchTaskData()
    override var taskData: TaskData? = null
    override var primaryKeys: List<String> = listOf()

    override fun fetchTaskData(request: FetchTasks.FetchTaskData.Request) {
        this.taskList = mTaskDataProvider.fetchTaskData()
        this.taskList = taskList

        val response = FetchTasks.FetchTaskData.Response(this.taskList)
        mPresenter.presentTaskData(response)
    }

    override fun updateAllCheckBox(isChecked: Boolean) {
        val taskList = mTaskDataProvider.fetchTaskData().map {
            TaskData(it.primaryKey, it.task, isChecked)
        }
        updateTaskData(UpdateTasks.UpdateTaskData.Request(taskList))
    }

    override fun updateTaskData(request: UpdateTasks.UpdateTaskData.Request) {
        this.taskList = request.taskList.map {
            TaskData(it.primaryKey, it.task, it.isChecked)
        }

        mTaskDataProvider.updateTaskData(this.taskList)

        this.taskList = mTaskDataProvider.fetchTaskData()
        val response = UpdateTasks.UpdateTaskData.Response(this.taskList)
        mPresenter.presentUpdateTaskData(response)
    }

    override fun deleteCheckedTaskData() {
        val taskList = mTaskDataProvider.fetchTaskData().map {
            TaskData(it.primaryKey, it.task, it.isChecked)
        }

        var primaryKeys = mutableListOf<Long>()
        for (it in taskList) {
            if (it.isChecked) {
                primaryKeys.add(it.primaryKey)
            }
        }

        deleteTaskData(DeleteTasks.DeleteTaskData.Request(primaryKeys))
    }

    override fun deleteTaskData(request: DeleteTasks.DeleteTaskData.Request) {
        mTaskDataProvider.deleteTaskData(request.primaryKeys)

        this.taskList = mTaskDataProvider.fetchTaskData()
        val response = DeleteTasks.DeleteTaskData.Response(this.taskList)
        mPresenter.presentDeleteTaskData(response)
    }
}

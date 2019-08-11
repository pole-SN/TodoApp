package com.kotlin.practice.todoapp.contents.scenes.todocontents

import com.kotlin.practice.todoapp.contents.models.TaskData

interface PresentationLogic {
    fun presentTaskData(response: FetchTasks.FetchTaskData.Response)
    fun presentUpdateTaskData(response: UpdateTasks.UpdateTaskData.Response)
    fun presentDeleteTaskData(response: DeleteTasks.DeleteTaskData.Response)
}

class TodoContentsPresenter : PresentationLogic {
    lateinit var mFragment: DisplayLogic

    override fun presentTaskData(response: FetchTasks.FetchTaskData.Response) {
        val taskList = response.taskList?.map {
            TaskData(it.primaryKey, it.task, it.isChecked)
        } ?: listOf()

        val viewModel = FetchTasks.FetchTaskData.ViewModel(
            taskList
        )
        mFragment.displayTaskData(viewModel)
    }

    override fun presentUpdateTaskData(response: UpdateTasks.UpdateTaskData.Response) {
        val viewModel = UpdateTasks.UpdateTaskData.ViewModel()
        mFragment.displayUpdateTaskData(viewModel)
    }

    override fun presentDeleteTaskData(response: DeleteTasks.DeleteTaskData.Response) {
        val viewModel = DeleteTasks.DeleteTaskData.ViewModel()
        mFragment.displayDeleteTaskData(viewModel)
    }
}

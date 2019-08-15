package com.kotlin.practice.todoapp.contents.model

import com.kotlin.practice.todoapp.contents.dataformat.TaskData
import io.realm.Realm
import io.realm.RealmObject
import io.realm.Sort
import io.realm.annotations.PrimaryKey

open class TaskDataObject(
    @PrimaryKey open var primaryKey: Long = 0,
    open var task: String = "",
    open var isChecked: Boolean = false
) : RealmObject() {

    companion object {
        fun from(taskData: TaskData): TaskDataObject {
            return TaskDataObject(
                taskData.primaryKey,
                taskData.task,
                taskData.isChecked
            )
        }
    }

    val taskData: TaskData
        get() = TaskData(
            primaryKey,
            task,
            isChecked
        )
}

interface TaskDataStoreInterface {
    fun fetchTaskData(): List<TaskData>
    fun addTaskData(task: String): TaskData
    fun updateTaskData(taskList: List<TaskData>)
    fun deleteTaskData(primaryKeys: List<Long>)
}

class RealmRepository : TaskDataStoreInterface {
    override fun fetchTaskData(): List<TaskData> {
        return Realm.getDefaultInstance().use {
            it.where(TaskDataObject::class.java)
                .findAll()
                .sort("primaryKey", Sort.ASCENDING)
                .map { it.taskData }
        }
    }

    override fun addTaskData(task: String): TaskData {
        val taskData = TaskData(
            getNextPrimaryKey(),
            task,
            false
        )
        Realm.getDefaultInstance().use {
            it.run {
                beginTransaction()
                val taskDataObject =
                    TaskDataObject.from(
                        taskData
                    )
                copyToRealmOrUpdate(taskDataObject)
                commitTransaction()
            }
        }
        return taskData
    }

    override fun updateTaskData(taskList: List<TaskData>) {
        Realm.getDefaultInstance().use {
            it.run {
                beginTransaction()
                for (taskData in taskList) {
                    val taskDataObject =
                        TaskDataObject.from(
                            taskData
                        )
                    copyToRealmOrUpdate(taskDataObject)
                }
                commitTransaction()
            }
        }
    }

    override fun deleteTaskData(primaryKeys: List<Long>) {
        Realm.getDefaultInstance().use {
            it.run {
                beginTransaction()
                for (primaryKey in primaryKeys) {
                    val taskDataObject = it.where(TaskDataObject::class.java)
                        .equalTo("primaryKey", primaryKey)
                        .findFirst()
                    taskDataObject?.deleteFromRealm()
                }
                commitTransaction()
            }
        }
    }

    private fun getNextPrimaryKey(): Long {
        var nextPrimaryKey: Long = 0
        val maxPrimaryKey =
            Realm.getDefaultInstance().use {
                it.where(TaskDataObject::class.java).max("primaryKey")
            }
        if (maxPrimaryKey != null) {
            nextPrimaryKey = maxPrimaryKey.toLong() + 1L
        }
        return nextPrimaryKey
    }
}

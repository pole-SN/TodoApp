package com.kotlin.practice.todoapp.contents.externals

import com.kotlin.practice.todoapp.contents.models.TaskData
import com.kotlin.practice.todoapp.contents.providers.TaskDataStoreInterface
import io.realm.Realm
import io.realm.RealmObject
import io.realm.Sort
import io.realm.annotations.PrimaryKey

open class TaskDataObject(
    @PrimaryKey open var primaryKey: Int = 0,
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

class TaskDataRealmStore : TaskDataStoreInterface {
    override fun fetchTaskData(): List<TaskData>? {
        return Realm.getDefaultInstance().use {
            it.where(TaskDataObject::class.java)
                .findAll()
                .sort("primaryKey", Sort.ASCENDING)
                .map { it.taskData }
        }
    }

    override fun updateTaskData(taskList: List<TaskData>?) {
        taskList?.forEach {
            val taskDataObject = TaskDataObject.from(it)
            if (taskDataObject.primaryKey == 0) taskDataObject.primaryKey = getNextPrimaryKey()
            Realm.getDefaultInstance().use {
                it.run {
                    beginTransaction()
                    copyToRealmOrUpdate(taskDataObject)
                    commitTransaction()
                }
            }
        }
    }

    override fun deleteTaskData(primaryKeys: List<Int>) {
        primaryKeys.forEach {
            var primaryKey = it
            Realm.getDefaultInstance().use {
                val taskDataObject = it.where(TaskDataObject::class.java)
                    .equalTo("primaryKey", primaryKey)
                    .findFirst()

                it.run {
                    beginTransaction()
                    taskDataObject?.deleteFromRealm()
                    commitTransaction()
                }
            }
        }
    }

    private fun getNextPrimaryKey(): Int {
        var nextPrimaryKey: Int = 1
        val maxPrimaryKey =
            Realm.getDefaultInstance().use {
                it.where(TaskDataObject::class.java).max("primaryKey")
            }
        if (maxPrimaryKey != null) {
            nextPrimaryKey = maxPrimaryKey.toInt() + 1
        }
        return nextPrimaryKey
    }
}

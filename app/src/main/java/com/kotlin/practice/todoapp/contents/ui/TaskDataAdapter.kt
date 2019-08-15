package com.kotlin.practice.todoapp.contents.ui

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.practice.todoapp.R
import com.kotlin.practice.todoapp.contents.dataformat.TaskData
import kotlinx.android.synthetic.main.list_item_content.view.*


class TaskDataViewHolder(view: View) : RecyclerView.ViewHolder(view)

class TaskDataAdapter(
    private val mTaskList: List<TaskData>,
    private val mListener: TodoContentsFragment.ListRequestListener
) : RecyclerView.Adapter<TaskDataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskDataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(
            R.layout.list_item_content,
            parent,
            false
        )
        return TaskDataViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskDataViewHolder, position: Int) {
        val latestTaskData = mTaskList[position]
        val view: View = holder.itemView

        view.task_text_view.text = latestTaskData.task
        view.task_checkbox.isChecked = latestTaskData.isChecked

        view.task_delete_button.setOnClickListener {
            var primaryKeys: List<Long> = listOf(latestTaskData.primaryKey)
            mListener.onTaskDataDelete(primaryKeys)
        }

        view.task_checkbox.setOnClickListener {
            var taskData = TaskData(
                latestTaskData.primaryKey,
                latestTaskData.task,
                view.task_checkbox.isChecked
            )
            mListener.onTaskDataUpdate(taskData)
        }

        val paint: Paint = view.task_text_view.paint
        if (view.task_checkbox.isChecked) {
            paint.flags = paint.flags or Paint.STRIKE_THRU_TEXT_FLAG
            paint.isAntiAlias = true
            view.task_text_view.setTextColor(Color.GRAY)
            view.task_delete_button.visibility = View.VISIBLE
        } else {
            paint.flags = paint.flags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            paint.isAntiAlias = false
            view.task_text_view.setTextColor(Color.BLACK)
            view.task_delete_button.visibility = View.GONE
        }

        when (mListener.getRadioId()) {
            R.id.radio_button_2 -> {
                if (latestTaskData.isChecked) {
                    view.visibility = View.GONE
                } else {
                    view.visibility = View.VISIBLE
                }
            }
            R.id.radio_button_3 -> {
                if (latestTaskData.isChecked) {
                    view.visibility = View.VISIBLE
                } else {
                    view.visibility = View.GONE
                }
            }
            else -> {
                view.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return mTaskList.size
    }

    override fun getItemId(position: Int): Long {
        val id = mTaskList[position].primaryKey
        return id
    }
}
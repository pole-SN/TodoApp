package com.kotlin.practice.todoapp.contents.scenes.todocontents

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.practice.todoapp.R
import com.kotlin.practice.todoapp.contents.models.TaskData
import kotlinx.android.synthetic.main.list_item_content.view.*


class TaskDataViewHolder(view: View) : RecyclerView.ViewHolder(view)

class TaskDataAdapter(
    private val mTaskList: List<TaskData>,
    private val mListener: TodoContentsFragment.ListRequestListener,
    private val mListHeight: Int
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
        val taskData = mTaskList[position]
        val view: View = holder.itemView

        holder.run {
            mListener.onUiUpdate(mTaskList)

            view.task_text_view.text = taskData.task
            view.task_checkbox.isChecked = taskData.isChecked

            view.task_delete_button.setOnClickListener {
                var primaryKeys: List<Long> = listOf(taskData.primaryKey)
                mListener.onListDelete(primaryKeys)
            }

            view.task_checkbox.setOnClickListener {
                var passList = listOf(
                    TaskData(
                        taskData.primaryKey,
                        taskData.task,
                        view.task_checkbox.isChecked
                    )
                )
                mListener.onListUpdate(passList)
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

            view.layout_list_item.visibility = View.VISIBLE
            view.layout_list_item.layoutParams.height = mListHeight
            when (mListener.getRadioId()) {
                R.id.radio_button_2 -> {
                    if (taskData.isChecked) {
                        view.layout_list_item.visibility = View.GONE
                        view.layout_list_item.layoutParams.height = 0
                    }
                }
                R.id.radio_button_3 -> {
                    if (!taskData.isChecked) {
                        view.layout_list_item.visibility = View.GONE
                        view.layout_list_item.layoutParams.height = 0
                    }
                }
                else -> {
                    return
                }
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
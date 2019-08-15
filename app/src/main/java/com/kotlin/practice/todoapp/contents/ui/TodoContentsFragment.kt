package com.kotlin.practice.todoapp.contents.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.practice.todoapp.R
import com.kotlin.practice.todoapp.contents.dataformat.DeleteTasks
import com.kotlin.practice.todoapp.contents.dataformat.FetchTasks
import com.kotlin.practice.todoapp.contents.dataformat.TaskData
import com.kotlin.practice.todoapp.contents.dataformat.UpdateTasks
import com.kotlin.practice.todoapp.contents.usecase.TodoContentsUseCase
import kotlinx.android.synthetic.main.fragment_contents_list.*
import kotlinx.android.synthetic.main.fragment_contents_list.view.*


interface DisplayLogic {
    fun displayTaskData(response: FetchTasks.FetchTaskData.Response)
    fun displayAddTaskData(response: UpdateTasks.AddTaskData.Response)
    fun displaySingleUpdateTaskData(response: UpdateTasks.SingleUpdateTaskData.Response)
    fun displayMultiUpdateTaskData(response: UpdateTasks.MultiUpdateTaskData.Response)
    fun displayDeleteTaskData(response: DeleteTasks.DeleteTaskData.Response)
}

class TodoContentsFragment : Fragment(), DisplayLogic {

    private lateinit var mPresenter: TodoContentsPresenter

    private lateinit var mListener: ListRequestListener
    private lateinit var mTaskList: MutableList<TaskData>

    private lateinit var mClearCompleted: Button
    private lateinit var mRadioGroup: RadioGroup
    private lateinit var mItemCount: TextView

    init {
        setup()
    }

    private fun setup() {
        mPresenter = TodoContentsPresenter()
        mPresenter.mFragment = this

        val useCase = TodoContentsUseCase()
        useCase.mPresenter = mPresenter
        mPresenter.mUseCase = useCase

        val listener = ListRequestListener()
        this.mListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_contents_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPresenter.fetchTaskData()

        setupViews(view)
    }

    private fun setupViews(view: View) {
        view.input_text_view.setOnEditorActionListener { v, id, _ ->
            if ((id == EditorInfo.IME_ACTION_NEXT)
                || (id == EditorInfo.IME_ACTION_DONE)
            ) {
                var task = view.input_text_view.text.toString()
                if (task.isNotBlank()) {
                    mPresenter.addTaskData(task)
                }
                v.input_text_view.text.clear()
                true
            } else {
                false
            }
        }

        view.recycler_view.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.addItemDecoration(
                CustomItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL,
                    resources.getDimensionPixelSize(R.dimen.listItemHeight)
                )
            )
            this.itemAnimator = null
            val adapter = TaskDataAdapter(
                mTaskList,
                mListener
            )
            adapter.setHasStableIds(true)
            this.adapter = adapter
        }

        view.allselect_toggle.setOnClickListener {
            val taskList = mTaskList.map {
                TaskData(
                    it.primaryKey,
                    it.task,
                    view.allselect_toggle.isChecked
                )
            }
            mPresenter.multiUpdateTaskData(taskList)
        }

        mClearCompleted = view.clear_completed_button
        mClearCompleted.setOnClickListener {
            mPresenter.deleteCheckedTaskData()
        }

        mRadioGroup = view.radio_group
        mRadioGroup.setOnCheckedChangeListener { _, _ ->
            view.recycler_view.adapter?.notifyDataSetChanged()
        }

        mItemCount = view.item_count
        mListener.onUiUpdate(mTaskList)
    }

    override fun displayTaskData(response: FetchTasks.FetchTaskData.Response) {
        mTaskList = response.taskList.toMutableList()
        recycler_view.adapter?.notifyDataSetChanged()
    }

    override fun displayAddTaskData(response: UpdateTasks.AddTaskData.Response) {
        mTaskList.add(response.taskData)
        recycler_view.adapter?.notifyItemInserted(mTaskList.indexOf(response.taskData))
        mListener.onUiUpdate(mTaskList)
    }

    override fun displaySingleUpdateTaskData(response: UpdateTasks.SingleUpdateTaskData.Response) {
        for (taskData in mTaskList) {
            if (response.taskData.primaryKey == taskData.primaryKey) {
                taskData.task = response.taskData.task
                taskData.isChecked = response.taskData.isChecked
                recycler_view.adapter?.notifyItemChanged(mTaskList.indexOf(taskData))
                break
            }
        }
        mListener.onUiUpdate(mTaskList)
    }

    override fun displayMultiUpdateTaskData(response: UpdateTasks.MultiUpdateTaskData.Response) {
        for (localData in mTaskList) {
            for (responseData in response.taskList) {
                if (responseData.primaryKey != localData.primaryKey) continue

                if (!responseData.task.equals(localData.task)
                    || !responseData.isChecked.equals(localData.isChecked)
                ) {
                    localData.task = responseData.task
                    localData.isChecked = responseData.isChecked
                }
                break
            }
        }
        recycler_view.adapter?.notifyDataSetChanged()
        mListener.onUiUpdate(mTaskList)
    }

    override fun displayDeleteTaskData(response: DeleteTasks.DeleteTaskData.Response) {
        for (deletedKey in response.primaryKeys) {
            for (localData in mTaskList.toList()) {
                if (localData.primaryKey != deletedKey) continue

                val index = mTaskList.indexOf(localData)
                mTaskList.removeAt(index)
                recycler_view.adapter?.notifyItemRemoved(index)
                break
            }
        }
        mListener.onUiUpdate(mTaskList)
    }

    interface ListRequest {
        fun onTaskDataUpdate(taskData: TaskData)
        fun onTaskDataDelete(primaryKeys: List<Long>)
        fun onUiUpdate(taskList: List<TaskData>)
    }

    inner class ListRequestListener :
        ListRequest {
        override fun onTaskDataUpdate(taskData: TaskData) {
            mPresenter.singleUpdateTaskData(taskData)
        }

        override fun onTaskDataDelete(primaryKeys: List<Long>) {
            mPresenter.deleteTaskData(primaryKeys)
        }

        override fun onUiUpdate(taskList: List<TaskData>) {
            setupDependsOnViews(taskList)
        }

        fun getRadioId(): Int {
            return mRadioGroup.checkedRadioButtonId
        }

        private fun setupDependsOnViews(taskList: List<TaskData>) {
            mClearCompleted.visibility = getClearCompletedVisibility(taskList)
            allselect_toggle.isChecked = isAllChecked(taskList)
            updateItemCount(taskList)
            var itemCount = recycler_view.adapter?.itemCount ?: 0
            if (itemCount > 0) {
                hooter.visibility = View.VISIBLE
                allselect_toggle.visibility = View.VISIBLE
            } else {
                hooter.visibility = View.GONE
                allselect_toggle.visibility = View.INVISIBLE
            }
        }

        private fun getClearCompletedVisibility(taskList: List<TaskData>): Int {
            for (it in taskList) {
                if (it.isChecked) return View.VISIBLE
            }
            return View.INVISIBLE
        }

        private fun isAllChecked(taskList: List<TaskData>): Boolean {
            for (it in taskList) {
                if (!it.isChecked) return false
            }
            return true
        }

        private fun updateItemCount(taskList: List<TaskData>) {
            var count = 0
            for (it in taskList) {
                if (!it.isChecked) count++
            }
            mItemCount.text = getItemCountString(count)
        }

        private fun getItemCountString(count: Int): String {
            when (count == 1) {
                true -> return String.format(resources.getString(R.string.item_left))
                false -> return String.format(resources.getString(R.string.items_left, count))
            }
        }
    }
}

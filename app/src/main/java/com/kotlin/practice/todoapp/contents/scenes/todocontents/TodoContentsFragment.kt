package com.kotlin.practice.todoapp.contents.scenes.todocontents

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
import com.kotlin.practice.todoapp.contents.models.TaskData
import kotlinx.android.synthetic.main.fragment_contents_list.*
import kotlinx.android.synthetic.main.fragment_contents_list.view.*


interface DisplayLogic {
    fun displayTaskData(response: FetchTasks.FetchTaskData.Response)
    fun displayUpdateTaskData(response: UpdateTasks.UpdateTaskData.Response)
    fun displayDeleteTaskData(response: DeleteTasks.DeleteTaskData.Response)
}

class TodoContentsFragment : Fragment(), DisplayLogic {

    private lateinit var mInteractor: BusinessLogic
    private lateinit var mListener: ListRequestListener
    private lateinit var mTaskList: MutableList<TaskData>

    private lateinit var mClearCompleted: Button
    private lateinit var mRadioGroup: RadioGroup
    private lateinit var mItemCount: TextView

    init {
        setup()
    }

    private fun setup() {
        val interactor = TodoContentsDataInteractor()
        val presenter = TodoContentsPresenter()

        this.mInteractor = interactor
        interactor.mPresenter = presenter
        presenter.mFragment = this

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

        fetchTaskData()

        setupViews(view)
    }

    private fun setupViews(view: View) {
        view.input_text_view.run {
            setOnEditorActionListener { v, id, event ->
                if ((id == EditorInfo.IME_ACTION_NEXT) || (id == EditorInfo.IME_ACTION_DONE)) {
                    var task = view.input_text_view.text.toString()
                    if (task.isNotBlank()) {
                        var passList: List<TaskData> = listOf(
                            TaskData(-1, task, false)
                        )
                        updateTaskData(passList)
                    }
                    v.input_text_view.text.clear()
                    true
                } else {
                    false
                }
            }
        }

        view.recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            val adapter = TaskDataAdapter(
                mTaskList,
                mListener,
                resources.getDimensionPixelSize(R.dimen.listHeight)
            )
            adapter.setHasStableIds(true)
            this.adapter = adapter
            this.adapter?.notifyDataSetChanged()
        }

        view.allselect_toggle.run {
            setOnClickListener {
                updateAllCheckBox(this.isChecked)
            }
        }

        mClearCompleted = view.clear_completed_button
        mClearCompleted.run {
            setOnClickListener {
                deleteCheckedTaskData()
            }
        }

        mRadioGroup = view.radio_group
        mRadioGroup.run {
            setOnCheckedChangeListener { group, checkId ->
                view.recycler_view.adapter?.notifyDataSetChanged()
            }
        }

        mItemCount = view.item_count
        mListener.onUiUpdate(mTaskList)
    }

    private fun fetchTaskData() {
        val request = FetchTasks.FetchTaskData.Request()
        mInteractor.fetchTaskData(request)
    }

    private fun updateAllCheckBox(isChecked: Boolean) {
        mInteractor.updateAllCheckBox(isChecked)
    }

    private fun updateTaskData(taskList: List<TaskData>) {
        val request = UpdateTasks.UpdateTaskData.Request(taskList)
        mInteractor.updateTaskData(request)
    }

    private fun deleteCheckedTaskData() {
        mInteractor.deleteCheckedTaskData()
    }

    private fun deleteTaskData(primaryKeys: List<Long>) {
        val request = DeleteTasks.DeleteTaskData.Request(primaryKeys)
        mInteractor.deleteTaskData(request)
    }

    override fun displayTaskData(response: FetchTasks.FetchTaskData.Response) {
        mTaskList = response.taskList.toMutableList()
        recycler_view.adapter?.notifyDataSetChanged()
    }

    override fun displayUpdateTaskData(response: UpdateTasks.UpdateTaskData.Response) {
        for (responseData in response.taskList) {
            var foundKey: Long = -1
            for (localData in mTaskList) {
                if (responseData.primaryKey == localData.primaryKey) {
                    foundKey = localData.primaryKey
                }
                if (foundKey > -1) {
                    if (!responseData.task.equals(localData.task)
                        || !responseData.isChecked.equals(localData.isChecked)
                    ) {
                        localData.task = responseData.task
                        localData.isChecked = responseData.isChecked
                        var position = mTaskList.indexOf(localData)
                        recycler_view.adapter?.notifyItemChanged(position)
                    }
                    break
                }
            }
            if (foundKey.equals(-1L)) {
                mTaskList.add(responseData)
                recycler_view.adapter?.notifyItemInserted(responseData.primaryKey.toInt())
            }
        }
    }

    override fun displayDeleteTaskData(response: DeleteTasks.DeleteTaskData.Response) {
        var foundKey: Long = -1
        var iterator = mTaskList.iterator()
        for (localData in iterator) {
            for (responseData in response.taskList) {
                if (responseData.primaryKey == localData.primaryKey) {
                    foundKey = localData.primaryKey
                    break
                }
            }
            if (foundKey.equals(-1L)) {
                var position = mTaskList.indexOf(localData)
                iterator.remove()
                recycler_view.adapter?.notifyItemRemoved(position)
            }
            foundKey = -1
        }
        mListener.onUiUpdate(mTaskList)
    }

    interface ListRequest {
        fun onListUpdate(taskList: List<TaskData>)
        fun onListDelete(primaryKeys: List<Long>)
        fun onUiUpdate(taskList: List<TaskData>)
    }

    inner class ListRequestListener : ListRequest {
        override fun onListDelete(primaryKeys: List<Long>) {
            deleteTaskData(primaryKeys)
        }

        override fun onListUpdate(taskList: List<TaskData>) {
            updateTaskData(taskList)
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

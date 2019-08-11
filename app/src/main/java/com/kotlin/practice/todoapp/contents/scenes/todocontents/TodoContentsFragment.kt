package com.kotlin.practice.todoapp.contents.scenes.todocontents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.practice.todoapp.R
import com.kotlin.practice.todoapp.contents.models.TaskData
import kotlinx.android.synthetic.main.fragment_contents_list.*
import kotlinx.android.synthetic.main.fragment_contents_list.view.*


interface DisplayLogic {
    fun displayTaskData(viewModel: FetchTasks.FetchTaskData.ViewModel)
    fun displayUpdateTaskData(viewModel: UpdateTasks.UpdateTaskData.ViewModel)
    fun displayDeleteTaskData(viewModel: DeleteTasks.DeleteTaskData.ViewModel)
}

class TodoContentsFragment : Fragment(), DisplayLogic {

    private lateinit var mInteractor: BusinessLogic
    private lateinit var mToggle: ToggleButton
    private lateinit var mClearCompleted: Button
    private lateinit var mRadioGroup: RadioGroup
    private lateinit var mItemCount: TextView
    private lateinit var mListener: ListRequestListener

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

        setupViews(view)

        fetchTaskData()
    }

    private fun setupViews(view: View) {
        view.input_text_view.run {
            setOnEditorActionListener { v, id, event ->
                if ((id == EditorInfo.IME_ACTION_NEXT) || (id == EditorInfo.IME_ACTION_DONE)) {
                    var task = view.input_text_view.text.toString()
                    if (task.isNotBlank()) {
                        var passList: List<TaskData> = listOf(
                            TaskData(0, task, false)
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
        }

        mToggle = view.allselect_toggle
        mToggle.run {
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

    private fun deleteTaskData(primaryKeys: List<Int>) {
        val request = DeleteTasks.DeleteTaskData.Request(primaryKeys)
        mInteractor.deleteTaskData(request)
    }

    override fun displayTaskData(viewModel: FetchTasks.FetchTaskData.ViewModel) {
        recycler_view.adapter =
            TaskDataAdapter(
                viewModel.taskList,
                mListener,
                resources.getDimensionPixelSize(R.dimen.listHeight)
            )
        val adapter = recycler_view.adapter
        if (adapter?.itemCount == 0) {
            hooter.visibility = View.GONE
            mToggle.visibility = View.INVISIBLE
        } else {
            hooter.visibility = View.VISIBLE
            mToggle.visibility = View.VISIBLE
        }
    }

    override fun displayUpdateTaskData(viewModel: UpdateTasks.UpdateTaskData.ViewModel) {
        fetchTaskData()
    }

    override fun displayDeleteTaskData(viewModel: DeleteTasks.DeleteTaskData.ViewModel) {
        fetchTaskData()
    }

    interface ListRequest {
        fun onListUpdate(taskList: List<TaskData>)
        fun onListDelete(primaryKeys: List<Int>)
        fun onUiUpdate(taskList: List<TaskData>)
    }

    inner class ListRequestListener : ListRequest {
        override fun onListDelete(primaryKeys: List<Int>) {
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
            mToggle.isChecked = isAllChecked(taskList)
            updateItemCount(taskList)
        }

        private fun getClearCompletedVisibility(taskList: List<TaskData>): Int {
            taskList.forEach {
                if (it.isChecked) return View.VISIBLE
            }
            return View.INVISIBLE
        }

        private fun isAllChecked(taskList: List<TaskData>): Boolean {
            taskList.forEach {
                if (!it.isChecked) return false
            }
            return true
        }

        private fun updateItemCount(taskList: List<TaskData>) {
            var count = 0
            taskList.forEach {
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

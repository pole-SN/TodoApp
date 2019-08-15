package com.kotlin.practice.todoapp.root

import com.kotlin.practice.todoapp.R
import com.kotlin.practice.todoapp.contents.ui.TodoContentsFragment

interface FragmentReplacerInterface {
    fun replaceToTodoContentsFragment()
}

class FragmentReplacer : FragmentReplacerInterface {
    lateinit var mActivity: RootActivity

    override fun replaceToTodoContentsFragment() {
        val fragment = TodoContentsFragment()

        mActivity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_base, fragment)
            .commit()
    }
}

package com.kotlin.practice.todoapp.root

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.practice.todoapp.R

class RootActivity : AppCompatActivity() {
    private var mFragmentReplacer: FragmentReplacerInterface

    init {
        val fragmentReplacer = FragmentReplacer()

        this.mFragmentReplacer = fragmentReplacer
        fragmentReplacer.mActivity = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFragmentReplacer.replaceToTodoContentsFragment()
    }
}

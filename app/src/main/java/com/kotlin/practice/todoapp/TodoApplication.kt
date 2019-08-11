package com.kotlin.practice.todoapp

import android.app.Application
import io.realm.Realm

class TodoApplication : Application() {
    companion object {
        lateinit var instance: TodoApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        Realm.init(this)
    }
}

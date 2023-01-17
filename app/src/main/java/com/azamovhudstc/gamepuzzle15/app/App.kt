package com.azamovhudstc.gamepuzzle15.app

import android.app.Application
import com.azamovhudstc.gamepuzzle15.database.MySharedPreferences

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        // registration shared preferences
        MySharedPreferences.initPreferences(this)
    }
}
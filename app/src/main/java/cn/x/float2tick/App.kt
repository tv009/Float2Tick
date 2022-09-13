package cn.x.float2tick

import android.app.Application
import android.content.Intent

class App : Application() {
    companion object {
        lateinit var instance : Application
    }
    private var backServiceIntent :Intent? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        backServiceIntent =  Intent(this, FloatingTimeService::class.java)
        startService(backServiceIntent)
    }

    override fun onTerminate() {
        super.onTerminate()
        stopService(backServiceIntent)
    }
}
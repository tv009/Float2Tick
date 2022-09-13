package cn.x.float2tick

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.*
import android.provider.Settings
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import cn.x.float2tick.listener.FloatingOnTouchListener
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class FloatingTimeService : Service() {
    private var windowManager: WindowManager? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var button: TextView? = null

    //显示秒 则 500ms 刷新一次
    var formatter_s = SimpleDateFormat("当前时间 HH:mm:ss")
    var last_size = 0

    override fun onCreate() {
        super.onCreate()
        isStarted = true

        initWindowView()
        startUpdateTimer()
    }

    private fun initWindowView() {
        windowManager = getSystemService() //getSystemService(WINDOW_SERVICE) as WindowManager
        layoutParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams?.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        layoutParams?.format = PixelFormat.RGBA_8888
        //        layoutParams.alpha = 0.5f;
        layoutParams?.gravity = Gravity.START or Gravity.TOP
        layoutParams?.flags =  //不拦截触摸事件
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or  //保持常亮
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or  //在整个屏幕, 不管状态栏
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or  //在窗口外可以处理触摸, 且监听窗口外的点击
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
        layoutParams?.width = -2
        layoutParams?.height = -2
        layoutParams?.x = 300
        layoutParams?.y = 300
    }

    private fun startUpdateTimer() {
        Handler(Looper.getMainLooper()).postDelayed({
            val nowData = Date()
            val num = System.currentTimeMillis()%1000
            val lastStr = when(last_size){
                1 -> ":"+ (num/100).toInt()
                2 -> ":"+ (num/10).toInt()
                3 -> ":$num"
                else -> ""
            }
            if (button != null) button!!.text = formatter_s.format(nowData)+lastStr
            startUpdateTimer()
        }, 80)
    }

    private val mybinder: Binder = Mybinder()

    inner class Mybinder : Binder() {
        fun updateConfig() {}
        @RequiresApi(Build.VERSION_CODES.M)
        fun startTimer() {
            showFloatingWindow()
        }
        fun stopTimer() {
            windowManager?.removeViewImmediate(timeView)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return mybinder
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    var timeView :View? = null

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            val layoutInflater = LayoutInflater.from(this)
            val displayView = layoutInflater.inflate(R.layout.float_time_big, null)
            button = displayView.findViewById(R.id.tv_now_time)
            displayView.findViewById<ImageView>(R.id.iv_close).setOnClickListener { (mybinder as Mybinder).stopTimer() }
//            button?.setTextColor(Color.WHITE)
            button?.text = "当前时间: 计算中"
            timeView = displayView
//            val lp = WindowManager.LayoutParams()
            windowManager!!.addView(displayView, layoutParams)
            displayView?.setOnTouchListener(FloatingOnTouchListener(windowManager,layoutParams))
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        isStarted = false
    }

    companion object {
        var isStarted = false
    }
}
package cn.x.float2tick.listener

import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

class FloatingOnTouchListener(
    val windowManager: WindowManager?,
    val layoutParams: WindowManager.LayoutParams?
) : View.OnTouchListener {
    private var x = 0
    private var y = 0
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                x = event.rawX.toInt()
                y = event.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val nowX = event.rawX.toInt()
                val nowY = event.rawY.toInt()
                val movedX = nowX - x
                val movedY = nowY - y
                x = nowX
                y = nowY
                layoutParams?.apply {
                    x += movedX
                    y += movedY
                    windowManager?.updateViewLayout(view, this)
                }


            }
            else -> {}
        }
        return false
    }
}
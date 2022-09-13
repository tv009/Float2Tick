package cn.x.float2tick

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import cn.x.float2tick.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.simpleName
    var floatBinder :FloatingTimeService.Mybinder? = null

    lateinit var binding :ActivityMainBinding

    val connection = object: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            Log.d(TAG,"onServiceConnected()")
            binder?.let {
                floatBinder = it as FloatingTimeService.Mybinder
                Log.d(TAG,"onServiceConnected() succ")
            }

        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.d(TAG,"onServiceDisconnected()")
            floatBinder = null
        }

    }
    var isStart = false
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindService(Intent(App.instance, FloatingTimeService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )

        binding.fab.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT)
                startActivityForResult(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse(
                            "package:$packageName"
                        )
                    ), 0
                )
            }else{
                isStart = !isStart
                if(isStart) floatBinder?.startTimer() else floatBinder?.stopTimer()
            }
        }




    }




    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show()
//                startService(Intent(this@MainActivity, FloatingButtonService::class.java))
                isStart = !isStart
                if(isStart) floatBinder?.startTimer() else floatBinder?.stopTimer()
            }
        }
    }

}
package ru.litov74dev.waterlock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ru.litov74dev.waterlock.databinding.OverlayLayoutBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: OverlayLayoutBinding
    private lateinit var overlayView: View

    companion object {
        const val REQUEST_OVERLAY_PERMISSION = 1234
        const val REQUEST_ACTIVITY_END = "REQUEST_ACTIVITY_END"

        fun launchActivity(context: Context) {
            context.startActivity(
                Intent(context, MainActivity::class.java)
/*
                    .setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
*/
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    private val broadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val data = intent?.getIntExtra(REQUEST_ACTIVITY_END, 0)
            data?.let {
                if (it == 1) finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OverlayLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
            } else {
                showOverlay()
            }
        } else {
            showOverlay()
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver, IntentFilter("end-event")
        )

        val i = Intent(Intent.ACTION_MAIN)
        i.addCategory(Intent.CATEGORY_HOME)
        startActivity(i)

    }

    private fun showOverlay() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        overlayView = inflater.inflate(R.layout.overlay_layout, null)

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(overlayView, layoutParams)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                showOverlay()
            } else {
                // похуй
            }
        }
    }

    override fun finish() {
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.removeView(overlayView)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.finish()
    }


}

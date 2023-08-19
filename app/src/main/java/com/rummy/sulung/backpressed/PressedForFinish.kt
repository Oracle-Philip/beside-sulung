package com.rummy.sulung.backpressed

import android.app.Activity
import android.os.Build
import android.os.Process
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.rummy.sulung.R

class PressedForFinish(  // 종료할 액티비티의 Activity 객체
    private val activity: Activity
) {
    val TAG = javaClass.simpleName
    private var backKeyPressedTime: Long = 0
    private val TIME_INTERVAL: Long = 2000
    private var stoast
            : Toast? = null
    fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + TIME_INTERVAL) {
            backKeyPressedTime = System.currentTimeMillis()
            showMessage()
        } else {
            stoast!!.cancel()
            try {
                activity.moveTaskToBack(true)
                activity.finishAndRemoveTask()
                activity.finish()
                activity.finishAffinity()
                activity.overridePendingTransition(0, 0)
                Process.killProcess(Process.myPid())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun showMessage() {
        stoast = Toast.makeText(
            activity,
            activity.getString(R.string.back_button_pop_up),
            Toast.LENGTH_SHORT
        )
        stoast?.show()
    }
}
package com.rummy.sulung.network

import android.content.Intent
import android.util.Log
import com.rummy.sulung.common.App
import com.rummy.sulung.common.Prefs
import com.rummy.sulung.view.LoginActivity
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class UserInfoTokenAuthenticator : Authenticator {
    val TAG = UserInfoTokenAuthenticator::class.java.simpleName
    override fun authenticate(
        route: Route?,
        response: Response
    ): Request? {
        Log.e(TAG, response.code().toString())
        if (response.code() == 401) {
            Log.e(TAG, "error 401")
            App.instance.run {
                val intent = Intent(App.instance, LoginActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            }
        }
        return null
    }
}
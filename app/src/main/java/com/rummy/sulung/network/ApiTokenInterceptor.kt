package com.rummy.sulung.network

import android.util.Log
import com.rummy.sulung.common.Prefs
import okhttp3.Interceptor
import okhttp3.Response

class ApiTokenInterceptor : Interceptor {
    val TAG = ApiTokenInterceptor::class.java.simpleName

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder().apply {
            Prefs.Token?.let { header("Authorization", "Bearer $it") }
            method(original.method(), original.body())
        }.build()

        Log.e(TAG, "request $request")

        return chain.proceed(request)
    }
}
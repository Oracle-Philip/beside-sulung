package com.rummy.sulung.network

import android.util.Log
import com.rummy.sulung.common.Prefs
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator : Authenticator {
    val TAG = TokenAuthenticator::class.java.simpleName
    override fun authenticate(
        route: Route?,
        response: Response
    ): Request? {
        Log.e(TAG, response.code().toString())
        if (response.code() == 401) {
            return runBlocking {
                //val tokenResponse = refreshToken()

                //Log.e(TAG, "tokenResponse:  ${tokenResponse.toString()}")

//                if (tokenResponse) {
//                    //debug("토큰 갱신 성공")
                //Prefs.token = tokenResponse.data
//                } else {
//                    error("토큰 갱신 실패.")
//                    Prefs.token = null
//                    Prefs.refreshToken = null
//                }
//seq22
                //Log.e(TAG, "re token : ${Prefs.refreshToken.toString()}")
                Prefs.Token?.let { it ->
                    //Log.e(TAG, "token : ${refreshToken}")
                    //debug("토큰 = $token")
                    response.request()
                        .newBuilder()
                        .header("Authorization", "Bearer $it")
                        .build()
                }
            }
        }

        return null
    }
}
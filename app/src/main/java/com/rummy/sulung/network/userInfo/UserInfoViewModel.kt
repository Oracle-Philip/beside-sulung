package com.rummy.sulung.network.userInfo

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rummy.sulung.common.App
import com.rummy.sulung.network.UserInfoAPI.UserInfoAPI
import com.rummy.sulung.network.service.SulungApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.SocketTimeoutException

class UserInfoViewModel : ViewModel()  {
    val TAG = UserInfoViewModel::class.java.simpleName

    val userInfoResponseData = MutableLiveData<UserInfoResponse>()

    suspend fun RequestUserInfo(): Response<UserInfoResponse> =
        withContext(Dispatchers.IO){
            UserInfoAPI.instance.userInfo()
        }

    suspend fun UserInfo(): Response<UserInfoResponse>? {
        try{
            val response = RequestUserInfo()
            onUserInfoResponse(response)
            return response
        } catch (e: Exception){
            withContext(Dispatchers.Main) {
                Toast.makeText(App.instance, "알 수 없는 오류가 발생했습니다. $e", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SocketTimeoutException){
            val response = RequestUserInfo()
            onUserInfoResponse(response)
            return response
        }
        return null
    }

    private fun onUserInfoResponse(response: Response<UserInfoResponse>) {
        if (response.isSuccessful) {
            val result: UserInfoResponse? = response.body()
            userInfoResponseData.postValue(result!!)
        }else{

        }
    }
}

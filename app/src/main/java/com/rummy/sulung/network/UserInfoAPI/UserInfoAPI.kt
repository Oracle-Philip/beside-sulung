package com.rummy.sulung.network.UserInfoAPI

import com.rummy.sulung.network.ApiGenerator
import com.rummy.sulung.network.userInfo.UserInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserInfoAPI {
    @GET("/user-info")
    suspend fun userInfo()
            : Response<UserInfoResponse>

    companion object {
        val instance = ApiGenerator()
            .generateUserInfo(UserInfoAPI::class.java)
    }
}
package com.rummy.sulung.network.response

import com.squareup.moshi.Json

data class SigninResponse(
    @field:Json(name = "token")
    val token: String,
    @field:Json(name = "nickname")
    val nickname: String,
    @field:Json(name = "userTerms")
    val userTerms: List<UserTerm?>?
)

data class UserTerm(
    @field:Json(name = "isConsented")
    val isConsented: Boolean?,
    @field:Json(name = "termsType")
    val termsType: Int?
)
package com.rummy.sulung.network.response


import com.squareup.moshi.Json

data class DeleteUserResponse(
    @Json(name = "message")
    val message: Any?,
    @Json(name = "status")
    val status: String?
)
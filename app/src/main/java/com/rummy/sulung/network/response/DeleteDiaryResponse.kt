package com.rummy.sulung.network.response


import com.squareup.moshi.Json

data class DeleteDiaryResponse(
    @field:Json(name = "body")
    val body: Any?,
    @field:Json(name = "statusCode")
    val statusCode: String?,
    @field:Json(name = "statusCodeValue")
    val statusCodeValue: Int?
)
package com.rummy.sulung.network.response


import com.squareup.moshi.Json

data class TermsConcentedResponse(
    @field:Json(name = "status")
    val status: String,
    @field:Json(name = "message")
    val message: String?,
)
package com.rummy.sulung.network.request


import com.squareup.moshi.Json

data class RegDiaryEasilyResponse(
    @field:Json(name = "id")
    val id: Int?
)
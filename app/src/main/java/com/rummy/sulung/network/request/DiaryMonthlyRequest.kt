package com.rummy.sulung.network.request

data class DiaryMonthlyRequest(
    val date : String,
    val size : Int? = 80,
    val sort : String? = "NEWEST"
)


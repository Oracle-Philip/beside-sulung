package com.rummy.sulung.network.request

data class StoreListMonthlyRequest(
    val drinkType: String?,
    val emotion: String?,
    val date: String,
    val size: Int?
)

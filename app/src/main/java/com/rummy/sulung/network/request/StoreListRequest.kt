package com.rummy.sulung.network.request

data class StoreListRequest(
    val drinkType: String?,
    val emotion: String?,
    val preCursor: Int?,
    val nextCursor: Int?,
    val size: Int?
)


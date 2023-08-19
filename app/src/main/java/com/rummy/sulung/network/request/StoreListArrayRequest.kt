package com.rummy.sulung.network.request

data class StoreListArrayRequest(
    val drinkType: Array<Int>?,
    val emotion: Array<Int>?,
    val preCursor: Int?,
    val nextCursor: Int?,
    val size: Int?
)
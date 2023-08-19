package com.rummy.sulung.network.request

data class DiaryListRequest(
    val preCursor : Int?,
    val nextCursor : Int?,
    val size : Int?,
    val sort : String?
)


package com.rummy.sulung.network.response


import com.squareup.moshi.Json

data class GetStoreMonthlyResponse(
    @field:Json(name = "id")
    val id: Int,
    @field:Json(name = "focus")
    val focus: Int?,
    @field:Json(name = "items")
    val items: List<Item>?,
    @field:Json(name = "nextCursor")
    val nextCursor: Int?,
    @field:Json(name = "preCursor")
    val preCursor: Int?
)

/*
data class ItemX(
    @Json(name = "date")
    val date: String?,
    @Json(name = "diaries")
    val diaries: List<DiaryX>?
)

data class DiaryX(
    @Json(name = "diaryDt")
    val diaryDt: Long?,
    @Json(name = "drinkType")
    val drinkType: Int?,
    @Json(name = "emotion")
    val emotion: Int?,
    @Json(name = "id")
    val id: Int?
)*/

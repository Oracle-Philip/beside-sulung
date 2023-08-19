package com.rummy.sulung.network.request


import com.squareup.moshi.Json

data class ReadDiaryListResponse2(
    @field:Json(name = "focus")
    val focus: Int?,
    @field:Json(name = "items")
    val items: List<Item?>?,
    @field:Json(name = "nextCursor")
    val nextCursor: Int?,
    @field:Json(name = "preCursor")
    val preCursor: Int?
)

data class Item(
    @field:Json(name = "date")
    val date: String?,
    @field:Json(name = "diaries")
    val diaries: List<Diary?>?
)

data class Diary(
    @field:Json(name = "content")
    val content: String?,
    @field:Json(name = "diaryDt")
    val diaryDt: Int?,
    @field:Json(name = "drinkType")
    val drinkType: Int?,
    @field:Json(name = "emotion")
    val emotion: Int?,
    @field:Json(name = "id")
    val id: Int?,
    @field:Json(name = "tag")
    val tag: String?
)
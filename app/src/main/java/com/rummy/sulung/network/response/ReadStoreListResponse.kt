package com.rummy.sulung.network.response


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "STORELIST")
data class ReadStoreListResponse(
    @PrimaryKey
    @field:Json(name = "id")
    val id: Int,
    @field:Json(name = "items")
    val items: List<Item?>?,
    @field:Json(name = "nextCursor")
    val nextCursor: Int?,
    @field:Json(name = "preCursor")
    val preCursor: Int?
)

sealed class DisplayableItem


data class Item(
    @field:Json(name = "date")
    val date: String?,
    @field:Json(name = "diaries")
    var diaries: List<Diary>?
)

data class Diary(
    @field:Json(name = "content")
    val content: String?,
    @field:Json(name = "diaryDt")
    val diaryDt: Long?,
    @field:Json(name = "drinkType")
    val drinkType: Int?,
    @field:Json(name = "emotion")
    val emotion: Int?,
    @field:Json(name = "id")
    val id: Int?,
    @field:Json(name = "tag")
    val tag: String?,
    @field:Json(name = "imageUrls")
    val imageUrls: List<String>,
) : DisplayableItem()

data class DateSeparator(val date: String) : DisplayableItem()

data class DateSeparatorDiary(val date: String) : DisplayableItem()

class SpanCountItem : DisplayableItem()

class SpanCountItemDiary : DisplayableItem()
/*
data class StoreItem(
    @field:Json(name = "date")
    val date: String?,
    @field:Json(name = "diaries")
    val diaries: List<StoreDiary?>?
)

data class StoreDiary(
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
)*/

package com.rummy.sulung.network.response


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "DIARYLIST")
data class ReadDiaryListResponse(
    @PrimaryKey
    @field:Json(name = "id")
    val id: Int,
    @field:Json(name = "focus")
    val focus: Int?,
    @field:Json(name = "items")
    val items: List<Item>,
    @field:Json(name = "nextCursor")
    val nextCursor: Int?,
    @field:Json(name = "preCursor")
    val preCursor: Int?
)

//fun ReadDiaryListResponse.asDatabaseModel() :
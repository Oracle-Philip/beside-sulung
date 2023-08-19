package com.rummy.sulung.network.response


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "DIARYLISTYEARMONTH")
data class ReadDiaryYearMonthResponse(
    @PrimaryKey
    @field:Json(name = "id")
    val id: Int,
    @field:Json(name = "items")
    val items: List<String>
)
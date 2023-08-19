package com.rummy.sulung.network.request

import okhttp3.MultipartBody

data class DiaryDetailRegRequest(
    val content : String,
    val diaryDt : Long,
    val drinkCount : Double,
    val drinkUnit : String,
    val drinkName : String,
    val drinkType : Int,
    val emotion : Int,
    val id : Int,
    val imageFiles : List<MultipartBody.Part?>,
    val tag : String
)
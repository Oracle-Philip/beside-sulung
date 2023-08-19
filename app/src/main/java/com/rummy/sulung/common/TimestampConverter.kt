package com.rummy.sulung.common

import java.lang.Long.parseLong
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


object TimestampConverter {
    fun getDateTime(s: String): String? {
        try {
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val netDate = Date(parseLong(s) * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    fun MillToDate1(mills: Long): String? {
        val pattern = "MM/dd/yyyy"
        val formatter = SimpleDateFormat(pattern)
        return formatter.format(Timestamp(mills)) as String
    }

    fun MillToDate(mills: Long): String? {
        val pattern = "M월 d일 • EEEE"
        val formatter = SimpleDateFormat(pattern)
        return formatter.format(Timestamp(mills)) as String
    }

//    fun getDay(){
//        val cal = Calendar.getInstance()
//        cal.time = System.currentTimeMillis()
//        cal.get()
//        val dayNum = cal[Calendar.DAY_OF_WEEK]
//
//        when (dayNum) {
//            1 -> day = "일요일"
//            2 -> day = "월요일"
//            3 -> day = "화요일"
//            4 -> day = "수요일"
//            5 -> day = "목요일"
//            6 -> day = "금요일"
//            7 -> day = "토요일"
//        }
//    }
}
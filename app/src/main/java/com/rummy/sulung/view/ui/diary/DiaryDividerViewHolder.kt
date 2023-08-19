package com.rummy.sulung.view.ui.diary

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rummy.sulung.R
import com.rummy.sulung.common.DiaryImage
import com.rummy.sulung.databinding.DiaryListDividerItemBinding
import com.rummy.sulung.databinding.DiaryListItemBinding
import com.rummy.sulung.databinding.StoreListDividerItemBinding
import com.rummy.sulung.databinding.StoreListItemBinding
import com.rummy.sulung.network.response.*
import com.rummy.sulung.view.ui.diary.Detail_Diary_Activity
import com.rummy.sulung.view.ui.diary.Detail_Diary_Activity.Companion.RECORD_ID

class DiaryDividerViewHolder(binding: DiaryListDividerItemBinding) : RecyclerView.ViewHolder(binding.root) {
    var view: DiaryListDividerItemBinding
    init{
        view = binding
    }

    fun bind(date: DisplayableItem?){
        if (date != null) {
            date as DateSeparatorDiary
            Log.e("Store", "date : ${date.date}")
            val month = date.date.split("-")[1].toIntOrNull() ?: 0
            when(month) {
                1 -> { view.date.text = "1월" }
                2 -> { view.date.text = "2월" }
                3 -> { view.date.text = "3월" }
                4 -> { view.date.text = "4월" }
                5 -> { view.date.text = "5월" }
                6 -> { view.date.text = "6월" }
                7 -> { view.date.text = "7월" }
                8 -> { view.date.text = "8월" }
                9 -> { view.date.text = "9월" }
                10 -> { view.date.text = "10월" }
                11 -> { view.date.text = "11월" }
                12 -> { view.date.text = "12월" }
                else -> { /* 예외 처리 */ }
            }
        }
    }

    companion object{
        fun create(parent: ViewGroup): DiaryDividerViewHolder {
            val view = DiaryListDividerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DiaryDividerViewHolder(view)
        }
    }
}
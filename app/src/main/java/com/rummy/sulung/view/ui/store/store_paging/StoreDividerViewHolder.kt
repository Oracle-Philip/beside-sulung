package com.rummy.sulung.view.ui.store.store_paging

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rummy.sulung.R
import com.rummy.sulung.common.DiaryImage
import com.rummy.sulung.databinding.DiaryListItemBinding
import com.rummy.sulung.databinding.StoreListDividerItemBinding
import com.rummy.sulung.databinding.StoreListItemBinding
import com.rummy.sulung.network.response.DateSeparator
import com.rummy.sulung.network.response.Diary
import com.rummy.sulung.network.response.DisplayableItem
import com.rummy.sulung.network.response.Item
import com.rummy.sulung.view.ui.diary.Detail_Diary_Activity
import com.rummy.sulung.view.ui.diary.Detail_Diary_Activity.Companion.RECORD_ID

class StoreDividerViewHolder(binding: StoreListDividerItemBinding) : RecyclerView.ViewHolder(binding.root) {
    var view: StoreListDividerItemBinding
    init{
        view = binding
    }

    fun bind(date: DisplayableItem?){
        if (date != null) {
            date as DateSeparator
            Log.e("Store", "date : ${date.date}")
            val month = date.date.split("-")[1].toIntOrNull() ?: 0
            when(month) {
                1 -> { view.dateImg.setImageResource(R.drawable.store_1) }
                2 -> { view.dateImg.setImageResource(R.drawable.store_2) }
                3 -> { view.dateImg.setImageResource(R.drawable.store_3) }
                4 -> { view.dateImg.setImageResource(R.drawable.store_4) }
                5 -> { view.dateImg.setImageResource(R.drawable.store_5) }
                6 -> { view.dateImg.setImageResource(R.drawable.store_6) }
                7 -> { view.dateImg.setImageResource(R.drawable.store_7) }
                8 -> { view.dateImg.setImageResource(R.drawable.store_8) }
                9 -> { view.dateImg.setImageResource(R.drawable.store_9) }
                10 -> { view.dateImg.setImageResource(R.drawable.store_10) }
                11 -> { view.dateImg.setImageResource(R.drawable.store_11) }
                12 -> { view.dateImg.setImageResource(R.drawable.store_12) }
                else -> { /* 예외 처리 */ }
            }
        }
    }

/*    fun refresh() {
        notifyDataSetChanged()
    }*/

    companion object{
        fun create(parent: ViewGroup): StoreDividerViewHolder {
            val view = StoreListDividerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return StoreDividerViewHolder(view)
        }
    }
}
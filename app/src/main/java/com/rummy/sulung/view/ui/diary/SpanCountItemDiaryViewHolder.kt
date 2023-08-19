package com.rummy.sulung.view.ui.diary

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rummy.sulung.common.DiaryImage
import com.rummy.sulung.databinding.DiaryListItemBinding
import com.rummy.sulung.databinding.SpanCountItemBinding
import com.rummy.sulung.databinding.SpanCountItemDiaryBinding
import com.rummy.sulung.databinding.StoreListItemBinding
import com.rummy.sulung.network.response.*
import com.rummy.sulung.view.ui.diary.Detail_Diary_Activity
import com.rummy.sulung.view.ui.diary.Detail_Diary_Activity.Companion.RECORD_ID

class SpanCountItemDiaryViewHolder(binding: SpanCountItemDiaryBinding) : RecyclerView.ViewHolder(binding.root) {
    var view: SpanCountItemDiaryBinding
    init{
        view = binding
    }

    fun bind(spanCountItem: DisplayableItem?){
        if (spanCountItem != null) {

        }
    }

    companion object{
        fun create(parent: ViewGroup): SpanCountItemDiaryViewHolder {
            val view = SpanCountItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SpanCountItemDiaryViewHolder(view)
        }
    }
}
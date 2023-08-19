package com.rummy.sulung.view.ui.store.store_paging

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rummy.sulung.common.DiaryImage
import com.rummy.sulung.databinding.DiaryListItemBinding
import com.rummy.sulung.databinding.SpanCountItemBinding
import com.rummy.sulung.databinding.StoreListItemBinding
import com.rummy.sulung.network.response.*
import com.rummy.sulung.view.ui.diary.Detail_Diary_Activity
import com.rummy.sulung.view.ui.diary.Detail_Diary_Activity.Companion.RECORD_ID

class SpanCountItemViewHolder(binding: SpanCountItemBinding) : RecyclerView.ViewHolder(binding.root) {
    var view: SpanCountItemBinding
    init{
        view = binding
    }

    fun bind(spanCountItem: DisplayableItem?, position: Int){
        if (spanCountItem != null) {
            if (spanCountItem is SpanCountItem) {
                view.id.text = position.toString()
            }
        }
    }

    companion object{
        fun create(parent: ViewGroup): SpanCountItemViewHolder {
            val view = SpanCountItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SpanCountItemViewHolder(view)
        }
    }
}
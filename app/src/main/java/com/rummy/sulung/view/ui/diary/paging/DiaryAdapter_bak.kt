/*
package com.rummy.sulung.view.ui.diary.paging

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rummy.sulung.network.response.Diary

class DiaryAdapter_bak : PagingDataAdapter<Diary, DiaryViewHolder>(REPO_COMPARATOR) {
    val TAG = DiaryAdapter_bak::class.java.simpleName
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        return DiaryViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val bpItem = getItem(position)
        //Log.e(TAG, "bpItem $bpItem")
        if (bpItem != null) {
            holder.bind(bpItem)
        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Diary>() {
            override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean =
                oldItem.hashCode() == newItem.hashCode()

            override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean =
                oldItem == newItem
        }
    }
}*/

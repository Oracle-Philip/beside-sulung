package com.rummy.sulung.view.ui.diary

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rummy.sulung.R
import com.rummy.sulung.databinding.DiaryListDividerItemBinding
import com.rummy.sulung.databinding.DiaryListItemBinding
import com.rummy.sulung.databinding.SpanCountItemBinding
import com.rummy.sulung.databinding.SpanCountItemDiaryBinding
import com.rummy.sulung.databinding.StoreListDividerItemBinding
import com.rummy.sulung.databinding.StoreListItemBinding
import com.rummy.sulung.network.response.*
import com.rummy.sulung.view.ui.store.store_paging.SpanCountItemViewHolder
import com.rummy.sulung.view.ui.store.store_paging.StoreDividerViewHolder
import com.rummy.sulung.view.ui.store.store_paging.StoreViewHolder

class DiaryAdapter : PagedListAdapter<DisplayableItem, RecyclerView.ViewHolder>(REPO_COMPARATOR) {
    val TAG = DiaryAdapter::class.java.simpleName

    var onFinishedLoading: (() -> Unit)? = null

    var context : Context? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.getContext();
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            is DateSeparatorDiary -> R.layout.diary_list_divider_item
            is Diary -> R.layout.diary_list_item
            is SpanCountItemDiary -> R.layout.span_count_item_diary
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.diary_list_divider_item -> DiaryDividerViewHolder(
                DiaryListDividerItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.diary_list_item -> DiaryViewHolder(
                DiaryListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            )
            R.layout.span_count_item_diary -> SpanCountItemDiaryViewHolder(
                SpanCountItemDiaryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            when (holder) {
                is DiaryDividerViewHolder -> holder.bind(item)
                is DiaryViewHolder -> holder.bind(item)
                is SpanCountItemDiaryViewHolder -> holder.bind(item)
                else -> throw IllegalArgumentException("Invalid view type")
            }
        }
    }

    fun refresh() {
        notifyDataSetChanged()
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<DisplayableItem>() {
            override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean =
                oldItem == newItem
        }
    }
}
package com.rummy.sulung.view.ui.store.store_paging

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rummy.sulung.R
import com.rummy.sulung.databinding.DiaryListItemBinding
import com.rummy.sulung.databinding.SpanCountItemBinding
import com.rummy.sulung.databinding.StoreListDividerItemBinding
import com.rummy.sulung.databinding.StoreListItemBinding
import com.rummy.sulung.network.response.DateSeparator
import com.rummy.sulung.network.response.Diary
import com.rummy.sulung.network.response.DisplayableItem
import com.rummy.sulung.network.response.SpanCountItem

class StoreAdapter : PagedListAdapter<DisplayableItem, RecyclerView.ViewHolder>(REPO_COMPARATOR) {
    val TAG = StoreAdapter::class.java.simpleName

    var context : Context? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.getContext();
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            is DateSeparator -> R.layout.store_list_divider_item
            is Diary -> R.layout.store_list_item
            is SpanCountItem -> R.layout.span_count_item
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.store_list_divider_item -> StoreDividerViewHolder(
                StoreListDividerItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.store_list_item -> StoreViewHolder(
                StoreListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            )
            R.layout.span_count_item -> SpanCountItemViewHolder(
                SpanCountItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                false
                )
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = getItem(position)
            if (item != null) {
                when (holder) {
                    is StoreViewHolder -> holder.bind(item, position)
                    is StoreDividerViewHolder -> holder.bind(item)
                    is SpanCountItemViewHolder -> holder.bind(item, position)
                    else -> throw IllegalArgumentException("Invalid view type")
                }
            }
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
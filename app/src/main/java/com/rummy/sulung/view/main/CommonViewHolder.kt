package com.rummy.sulung.view.main

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class CommonViewHolder(
    binding: ViewDataBinding
) : RecyclerView.ViewHolder(binding.root){
    abstract fun bind(item: CommonItem)
}
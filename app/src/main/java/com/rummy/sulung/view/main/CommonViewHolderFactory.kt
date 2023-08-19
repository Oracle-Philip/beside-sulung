package com.rummy.sulung.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.rummy.sulung.R

object CommonViewHolderFactory {
    fun createViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        return when(viewType) {
            ViewType.MAIN_1.ordinal -> MainViewHolder1(getViewDataBinding(parent, R.layout.activity_main_1))
            ViewType.MAIN_2.ordinal -> MainViewHolder2(getViewDataBinding(parent, R.layout.activity_main_2))
            ViewType.MAIN_3.ordinal -> MainViewHolder3(getViewDataBinding(parent, R.layout.activity_main_3))
            else -> MainViewHolder1(getViewDataBinding(parent, R.layout.activity_main_1))
        }
    }

    private fun <T: ViewDataBinding> getViewDataBinding(parent: ViewGroup, layoutRes: Int): T {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            layoutRes,
            parent,
            false
        )
    }
}
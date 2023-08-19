package com.rummy.sulung.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerAdapter : FragmentStateAdapter {
    private var type = 0
    private var count = 0

    constructor(fragmentActivity: FragmentActivity) : super(fragmentActivity) {}
    constructor(fragment: Fragment) : super(fragment) {}
    constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle) : super(
        fragmentManager,
        lifecycle
    ) {
    }

    fun setType(type: Int) {
        this.type = type
    }

    fun setType(type: Int, count: Int) {
        this.type = type
        this.count = count
    }

    //Fragment 갯수
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return if (type == TYPE_VERTICAL_VIEWPAGER) {
            VerticalFragment(position)
        } else {
            VerticalFragment(position)
        }
    }

    companion object {
        const val TYPE_VERTICAL_VIEWPAGER = 1001
        const val TYPE_HORIAONTALL_VIEWPAGER = 1002
    }
}

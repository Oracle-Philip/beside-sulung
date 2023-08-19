package com.rummy.sulung.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.rummy.sulung.R

class VerticalFragment : Fragment {
    private var position = 0

    constructor() {}
    internal constructor(position: Int) {
        // Required empty public constructor
        this.position = position
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_vertical, container, false)
        val viewPager2: ViewPager2 = view.findViewById(R.id.viewPager2)
        val adapter = ViewPagerAdapter(this)
        adapter.setType(ViewPagerAdapter.TYPE_HORIAONTALL_VIEWPAGER, position)
        viewPager2.adapter = adapter
        return view
    }
}
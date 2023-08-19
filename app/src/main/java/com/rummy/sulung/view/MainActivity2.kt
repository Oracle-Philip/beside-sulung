//package com.rummy.sulung.view
//
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import android.view.*
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.google.android.material.snackbar.Snackbar
//import com.google.android.material.tabs.TabLayout
//import androidx.viewpager.widget.ViewPager
//import androidx.appcompat.app.AppCompatActivity
//import androidx.annotation.RequiresApi
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import androidx.viewpager2.widget.ViewPager2
//import com.rummy.sulung.R
//import com.rummy.sulung.backpressed.PressedForFinish
//import com.rummy.sulung.common.App.Companion.TAG
//import com.rummy.sulung.databinding.ActivityMain3Binding
//import com.rummy.sulung.databinding.ActivityMainnBinding
//import com.rummy.sulung.view.main.CommonItem
//import com.rummy.sulung.view.main.CommonViewAdapter
//import com.rummy.sulung.view.ui.main.SectionsPagerAdapter
//
//class MainActivity2 : AppCompatActivity() {
//    lateinit var pressedForFinish: PressedForFinish
//    private lateinit var binding: ActivityMainnBinding
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityMainnBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val viewPager: ViewPager2 = binding.viewPager2
//        val adapter = ViewPagerAdapter(this)
//        adapter.setType(ViewPagerAdapter.TYPE_VERTICAL_VIEWPAGER)
//
//        val list = arrayListOf<CommonItem>(CommonItem("MAIN_1"), CommonItem("MAIN_2"), CommonItem("MAIN_3"))
//
//        val adapter2 = CommonViewAdapter(binding, list)
//
//        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
//            var currentState = 0
//            var currentPos = 0
//
//            override fun onPageScrolled(
//                position: Int,
//                positionOffset: Float,
//                positionOffsetPixels: Int
//            ) {
//                if(currentState == ViewPager2.SCROLL_STATE_DRAGGING && currentPos == position) {
//                    if(currentPos == 0) binding.viewPager2.currentItem = 2
//                    else if(currentPos == 2) binding.viewPager2.currentItem = 0
//                }
//                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
//            }
//
//            override fun onPageSelected(position: Int) {
//                currentPos = position
//                Log.e(TAG, currentPos.toString())
//
//                if(currentPos == 2){
//                    binding.recordAddMain.visibility = View.INVISIBLE
//                }else{
//                    binding.recordAddMain.visibility = View.VISIBLE
//                }
//                super.onPageSelected(position)
//            }
//
//            override fun onPageScrollStateChanged(state: Int) {
//                currentState = state
//                Log.e(TAG, "state : ${currentState.toString()}")
//                super.onPageScrollStateChanged(state)
//            }
//        })
//
//        pressedForFinish = PressedForFinish(this)
//
//        viewPager.adapter = adapter2
//        binding?.recordAddMain?.setOnClickListener {
//
//        }
//    }
//
//    fun RecyclerView.addScrollListener(onScroll: (position: Int) -> Unit) {
//        var lastPosition = 0
//        addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (layoutManager is LinearLayoutManager) {
//                    val currentVisibleItemPosition =
//                        (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
//
//                    if (lastPosition != currentVisibleItemPosition && currentVisibleItemPosition != RecyclerView.NO_POSITION) {
//                        onScroll.invoke(currentVisibleItemPosition)
//                        lastPosition = currentVisibleItemPosition
//                    }
//                }
//            }
//        })
//    }
//
//    override fun onBackPressed() {
//        pressedForFinish.onBackPressed()
//    }
//}
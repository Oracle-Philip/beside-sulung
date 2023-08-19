package com.rummy.sulung.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.rummy.sulung.backpressed.PressedForFinish
import com.rummy.sulung.common.App
import com.rummy.sulung.common.App.Companion.TAG
import com.rummy.sulung.common.Prefs
import com.rummy.sulung.databinding.ActivityMainEmptyDataBinding
import com.rummy.sulung.network.userInfo.UserInfoViewModel
import com.rummy.sulung.view.main.CommonItem
import com.rummy.sulung.view.main.CommonViewAdapter
import com.rummy.sulung.view.ui.addRecord.RecordActivity
import com.rummy.sulung.view.ui.addRecord.RecordActivity_replace
import com.rummy.sulung.view.ui.addRecord.RecordActivity_replace.Companion.FIRST_RECORD
import com.rummy.sulung.viewmodel.diary.DiaryAndroidViewModel
import kotlinx.coroutines.launch

class EmptyDataMainActivity : AppCompatActivity() {
    lateinit var pressedForFinish: PressedForFinish
    private lateinit var binding: ActivityMainEmptyDataBinding

    var id : Int? = null

    val userInfoViewModel : UserInfoViewModel by lazy {
        ViewModelProvider(this).get(UserInfoViewModel::class.java)
    }

    val diaryAndroidViewModel : DiaryAndroidViewModel by lazy{
        ViewModelProvider(this, DiaryAndroidViewModel.Factory(App.instance))
            .get(DiaryAndroidViewModel::class.java)
    }

    var data : String? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            userInfoViewModel.UserInfo()
        }

        userInfoViewModel.userInfoResponseData.observe(this, Observer {
            if(it != null){
                //Toast.makeText(this@EmptyDataMainActivity, "Email ${it.email} \nid ${it.id}, nickname ${it.nickName}", Toast.LENGTH_LONG).show()
                data = "Email ${it.email} \nid ${it.id}, nickname ${it.nickName}"
                Log.e(TAG, "my token ${Prefs.Token}")
                this.id = it.id
            }
        })


        binding = ActivityMainEmptyDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager: ViewPager2 = binding.viewPager2
        val adapter = ViewPagerAdapter(this)
        adapter.setType(ViewPagerAdapter.TYPE_VERTICAL_VIEWPAGER)

        val list = arrayListOf<CommonItem>(CommonItem("MAIN_1"), CommonItem("MAIN_2"), CommonItem("MAIN_3"))

        val commonAdapter = CommonViewAdapter(binding, list)

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            var currentState = 0
            var currentPos = 0

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if(currentState == ViewPager2.SCROLL_STATE_DRAGGING && currentPos == position) {
                    if(currentPos == 0) binding.viewPager2.currentItem = 2
                    else if(currentPos == 2) binding.viewPager2.currentItem = 0
                }
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                currentPos = position
                Log.e(TAG, currentPos.toString())

                if(currentPos == 2){
                    binding.recordAddMain.visibility = View.INVISIBLE
                }else{
                    binding.recordAddMain.visibility = View.VISIBLE
                }
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                currentState = state
                super.onPageScrollStateChanged(state)
            }
        })

        pressedForFinish = PressedForFinish(this)

        viewPager.adapter = commonAdapter
        binding?.recordAddMain?.setOnClickListener {
            val intent = Intent(binding.root.context, RecordActivity_replace::class.java)
            intent.putExtra(FIRST_RECORD, true)
            startActivity(intent)
        }
    }

    fun RecyclerView.addScrollListener(onScroll: (position: Int) -> Unit) {
        var lastPosition = 0
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (layoutManager is LinearLayoutManager) {
                    val currentVisibleItemPosition =
                        (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    if (lastPosition != currentVisibleItemPosition && currentVisibleItemPosition != RecyclerView.NO_POSITION) {
                        onScroll.invoke(currentVisibleItemPosition)
                        lastPosition = currentVisibleItemPosition
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        pressedForFinish.onBackPressed()
    }
}
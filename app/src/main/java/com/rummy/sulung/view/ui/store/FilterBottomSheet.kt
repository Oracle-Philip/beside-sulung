package com.rummy.sulung.view.ui.store

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.internal.ViewUtils.dpToPx
import com.rummy.sulung.R
import com.rummy.sulung.common.App
import com.rummy.sulung.view.ui.diary.Editing_Item_Adapter

class FilterBottomSheet(context: Context, val sharedViewModel: SharedViewModel, itemWidth : Int) : LinearLayout(context) {

    private val alcoholList: RecyclerView
    private val feelingList: RecyclerView
    private val resetButton: AppCompatButton
    private val exitIcon: ImageView
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var resetButtonClickListener: OnResetButtonClickListener? = null
    private var adjustButtonClickListener: OnAdjustButtonClickListener? = null
    private var exitIconClickListener: OnExitIconClickListener? = null

    private val adjustButton: AppCompatButton
    private var selectedItemCount = 0

    private val alcoholAdapter : Filter_Item_Adapter
    private val feelingAdapter : Filter_Item_Adapter

    var selectedAlcoholItemCount = 0
    var selectedAlcoholItemValue = ""
    var selectedFeelingItemCount = 0
    var selectedFeelingItemValue = ""

    interface OnResetButtonClickListener {
        fun onResetButtonClick()
    }

    interface OnAdjustButtonClickListener {
        fun onAdjustButtonClick(select_filters : String)
    }

    interface OnExitIconClickListener {
        fun onExitIconClick()
    }

    var alcoholListItems = mutableListOf(
        Triple("전체", false, "0,1,2,3,4,5,6,7,8"),
        Triple("소주", false, "0"),
        Triple("맥주", false, "1"),
        Triple("막걸리", false, "2"),

        Triple("양주", false, "3"),
        Triple("칵테일", false, "4"),
        Triple("와인", false, "5"),
        Triple("사케", false, "6"),

        Triple("샴페인", false, "7"),
        Triple("하이볼", false, "8"),
    )

    var feelingListItems = mutableListOf(
        Triple("전체", false, "0,1,2,3,4,5,6"),
        Triple("기쁨", false, "0"),
        Triple("슬픔", false, "1"),
        Triple("우울", false, "2"),

        Triple("화남", false, "3"),
        Triple("평온", false, "4"),
        Triple("축하", false, "5"),
        Triple("취함", false, "6")
    )

    init {
        View.inflate(context, R.layout.bottomsheetdialog_filter_replace, this)
        adjustButton = findViewById(R.id.adjust_btn)
        resetButton = findViewById(R.id.reset_btn)
        exitIcon = findViewById(R.id.exitIcon)
        // Set horizontal spacing between items to 8dp
        val spacing = resources.getDimensionPixelSize(R.dimen.item_spacing)
        val itemDecoration = GridSpacingItemDecoration(4, spacing, false)

        val alcoholLayoutManager = GridLayoutManager(context, 4)
        alcoholLayoutManager.orientation = GridLayoutManager.VERTICAL
        alcoholList = findViewById(R.id.alcohol_list)
        alcoholList.addItemDecoration(itemDecoration)
        // Set LayoutManager for RecyclerViews
        alcoholList.layoutManager = alcoholLayoutManager
        alcoholAdapter = Filter_Item_Adapter(alcoholListItems, itemWidth).apply {
            setOnItemClickListener(object : Filter_Item_Adapter.OnItemClickListener {
                override fun onItemClick(values: Map<String, String>) {
                    if(values.containsKey("전체")){
                        selectedAlcoholItemCount = 9
                        for (i in 1 until alcoholListItems.size) {
                            val (name, _, value) = alcoholListItems[i]
                            alcoholListItems[i] = Triple(name, false, value)
                        }
                        notifyDataSetChanged()
                    }else{
                        selectedAlcoholItemCount = values.size
                    }
                    Log.e("selectedValues", values.toString())
                    val buttonFilterCountNumb = when (selectedAlcoholItemCount + selectedFeelingItemCount) {
                        0 -> ""
                        else -> " (${selectedAlcoholItemCount + selectedFeelingItemCount})"
                    }
                    adjustButton.text = "필터 적용${buttonFilterCountNumb}"
                    selectedAlcoholItemValue = "drinkType=${values.values.joinToString(",")}"
                }
            })
        }
        alcoholList.adapter = alcoholAdapter
        alcoholList.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return rv.scrollState != RecyclerView.SCROLL_STATE_IDLE
            }
        })

        val feelingLayoutManager = GridLayoutManager(context, 4)
        feelingLayoutManager.orientation = GridLayoutManager.VERTICAL
        feelingList = findViewById(R.id.feeling_list)
        feelingList.addItemDecoration(itemDecoration)
        feelingList.layoutManager = feelingLayoutManager
        feelingAdapter = Filter_Item_Adapter(feelingListItems, itemWidth).apply {
            setOnItemClickListener(object : Filter_Item_Adapter.OnItemClickListener {
                override fun onItemClick(values: Map<String, String>) {
                    // Handle selected values
                    if(values.containsKey("전체")){
                        selectedFeelingItemCount = 7
                        for (i in 1 until feelingListItems.size) {
                            val (name, _, value) = feelingListItems[i]
                            feelingListItems[i] = Triple(name, false, value)
                        }
                        notifyDataSetChanged()
                    }else{
                        selectedFeelingItemCount = values.size
                    }
                    Log.e("selectedValues", values.toString())
                    val buttonFilterCountNumb = when (selectedAlcoholItemCount + selectedFeelingItemCount) {
                        0 -> ""
                        else -> " (${selectedAlcoholItemCount + selectedFeelingItemCount})"
                    }
                    selectedFeelingItemValue = "emotion=${values.values.joinToString(",")}"
                    adjustButton.text = "필터 적용${buttonFilterCountNumb}"
                }
            })
        }
        feelingList.adapter = feelingAdapter
        feelingList.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return rv.scrollState != RecyclerView.SCROLL_STATE_IDLE
            }
        })

        resetButton.setOnClickListener {
            resetSelections()
            resetButtonClickListener?.onResetButtonClick()
        }

        adjustButton.setOnClickListener {
            sharedViewModel.updateSelectedFilters(selectedAlcoholItemValue + selectedFeelingItemValue)
            sharedViewModel.updateFilterCount(selectedAlcoholItemCount + selectedFeelingItemCount)
            adjustButtonClickListener?.onAdjustButtonClick(selectedAlcoholItemValue + selectedFeelingItemValue)
            bottomSheetDialog?.dismiss()
            selectedAlcoholItemCount = 0
            selectedFeelingItemCount = 0
            Log.e("adjustButton", selectedAlcoholItemValue + selectedFeelingItemValue)
            Log.e("adjustButton cnt", "${selectedAlcoholItemCount + selectedFeelingItemCount}")
        }

        exitIcon.setOnClickListener {
            for (i in 0 until alcoholListItems.size) {
                val (name, _, value) = alcoholListItems[i]
                alcoholListItems[i] = Triple(name, false, value)
            }

            for (i in 0 until feelingListItems.size) {
                val (name, _, value) = feelingListItems[i]
                feelingListItems[i] = Triple(name, false, value)
            }

            alcoholAdapter.notifyDataSetChanged()
            feelingAdapter.notifyDataSetChanged()
            selectedAlcoholItemCount = 0
            selectedAlcoholItemValue = ""
            selectedFeelingItemCount = 0
            selectedFeelingItemValue = ""
            alcoholAdapter.setSelectedValues(alcoholListItems)
            feelingAdapter.setSelectedValues(feelingListItems)

            applySelectionsFromViewModel()
            exitIconClickListener?.onExitIconClick()
            bottomSheetDialog?.dismiss()
        }
    }
    fun resetSelections() {
        for (i in 0 until alcoholListItems.size) {
            val (name, _, value) = alcoholListItems[i]
            alcoholListItems[i] = Triple(name, false, value)
        }

        for (i in 0 until feelingListItems.size) {
            val (name, _, value) = feelingListItems[i]
            feelingListItems[i] = Triple(name, false, value)
        }

        alcoholAdapter.notifyDataSetChanged()
        feelingAdapter.notifyDataSetChanged()
        selectedAlcoholItemCount = 0
        selectedAlcoholItemValue = ""
        selectedFeelingItemCount = 0
        selectedFeelingItemValue = ""
        alcoholAdapter.setSelectedValues(alcoholListItems)
        feelingAdapter.setSelectedValues(feelingListItems)
        sharedViewModel.updateSelectedFilters(selectedAlcoholItemValue + selectedFeelingItemValue)
        sharedViewModel.updateFilterCount(null)
        adjustButton.text = "필터 적용"
    }

    private fun applySelectionsFromViewModel() {
        selectedAlcoholItemCount = 0
        selectedFeelingItemCount = 0
        Log.e("before applySelectionsFromViewModel", "${selectedAlcoholItemCount}")
        Log.e("before applySelectionsFromViewModel", "${selectedFeelingItemCount}")
        val filters = sharedViewModel.selectedFilters.value ?: ""
        val filterCount = sharedViewModel.filterCount.value?.let { " ($it)" } ?: ""
        if (filters.isNotEmpty()) {
            val drinkTypeIndex = filters.indexOf("drinkType=")
            val emotionIndex = filters.indexOf("emotion=")
                if (drinkTypeIndex >= 0) {
                    val drinkTypeValues = if (emotionIndex != -1) {
                        filters.substring(drinkTypeIndex + 10, emotionIndex).split(",")
                    } else {
                        filters.substring(drinkTypeIndex + 10).split(",")
                    }
                    if(filters.contains( "drinkType=0,1,2,3,4,5,6,7,8")){
                        selectedAlcoholItemValue = "drinkType=${drinkTypeValues.joinToString(",")}"
                        alcoholListItems[0] = Triple("전체", true, "0,1,2,3,4,5,6,7,8")
                        for (value in drinkTypeValues) {
                            val index = alcoholListItems.indexOfFirst { it.third == value }
                            if (index >= 0 && index < alcoholListItems.size) {
                                val (name, _, value) = alcoholListItems[index]
                                if (alcoholListItems[index].first != "전체") {
                                    selectedAlcoholItemCount++
                                }
                            }
                        }
                        alcoholAdapter.setSelectedValues(alcoholListItems)
                    }else{
                        selectedAlcoholItemValue = "drinkType=${drinkTypeValues.joinToString(",")}"
                        for (value in drinkTypeValues) {
                            val index = alcoholListItems.indexOfFirst { it.third == value }
                            if (index >= 0 && index < alcoholListItems.size) {
                                val (name, _, value) = alcoholListItems[index]
                                alcoholListItems[index] = Triple(name, true, value)
                                if (alcoholListItems[index].first != "전체") {
                                    selectedAlcoholItemCount++
                                }
                            }
                        }
                        alcoholAdapter.setSelectedValues(alcoholListItems)
                    }
                }

                if (emotionIndex >= 0) {
                    val emotionValues = filters.substring(emotionIndex + 8).split(",")
                    if(filters.contains("emotion=0,1,2,3,4,5,6")){
                        selectedFeelingItemValue = "emotion=${emotionValues.joinToString(",")}"
                        feelingListItems[0] = Triple("전체", true, "0,1,2,3,4,5,6")
                        for (value in emotionValues) {
                            val index = feelingListItems.indexOfFirst { it.third == value }
                            if (index >= 0 && index < feelingListItems.size) {
                                val (name, _, value) = feelingListItems[index]
                                if (feelingListItems[index].first != "전체") {
                                    selectedFeelingItemCount++
                                }
                            }
                        }
                        feelingAdapter.setSelectedValues(feelingListItems)
                    }else{
                        selectedFeelingItemValue = "emotion=${emotionValues.joinToString(",")}"
                        for (value in emotionValues) {
                            val index = feelingListItems.indexOfFirst { it.third == value }
                            if (index >= 0 && index < feelingListItems.size) {
                                val (name, _, value) = feelingListItems[index]
                                feelingListItems[index] = Triple(name, true, value)
                                if (feelingListItems[index].first != "전체") {
                                    selectedFeelingItemCount++
                                }
                            }
                        }
                        feelingAdapter.setSelectedValues(feelingListItems)
                    }
                }
            }
        Log.e("after applySelectionsFromViewModel", "${selectedAlcoholItemCount}")
        Log.e("after applySelectionsFromViewModel", "${selectedFeelingItemCount}")
            adjustButton.text = "필터 적용${filterCount}"
    }

    fun show() {
        bottomSheetDialog = BottomSheetDialog(context, R.style.AppBottomSheetDialogTheme)
        if (parent != null) {
            (parent as ViewGroup).removeView(this)
        }
        bottomSheetDialog?.setContentView(this)
        bottomSheetDialog?.setCanceledOnTouchOutside(false)

        applySelectionsFromViewModel()

        bottomSheetDialog?.show()
    }

    fun dismiss() {
        bottomSheetDialog?.dismiss()
    }

    fun setOnResetButtonClickListener(listener: OnResetButtonClickListener) {
        resetButtonClickListener = listener
    }

    fun setOnAdjustButtonClickListener(listener: OnAdjustButtonClickListener) {
        adjustButtonClickListener = listener
    }

    fun setOnExitIconClickListener(listener: OnExitIconClickListener) {
        exitIconClickListener = listener
    }

    class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int, // Change from 8dp to 4dp
        private val includeEdge: Boolean,
        private val edgeSpacing: Int = 0
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount

            if (includeEdge) {
                if (column == 0) { // Left edge
                    outRect.left = edgeSpacing
                    outRect.right = spacing / 2
                } else if ((column + 1) % spanCount == 0) { // Right edge
                    outRect.left = spacing / 2
                    outRect.right = edgeSpacing
                } else { // Middle items
                    outRect.left = spacing / 2
                    outRect.right = spacing / 2
                }

                if (position < spanCount) { // Top edge
                    outRect.top = edgeSpacing
                    outRect.bottom = spacing / 2
                } else { // Non-top items
                    outRect.top = spacing / 2
                    outRect.bottom = spacing / 2
                }
            } else {
                outRect.left = column * spacing / spanCount
                outRect.right = spacing - (column + 1) * spacing / spanCount
                if (position >= spanCount) {
                    outRect.top = spacing
                }
            }
        }
    }
}
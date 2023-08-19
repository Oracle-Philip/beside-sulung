/*
package com.rummy.sulung.view.ui.store

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rummy.sulung.R

interface OnBottomSheetItemSelectListener {
    fun onItemSelected(itemType: String, item: Triple<String, Boolean, String>)
}

interface OnResetButtonClickListener {
    fun onResetButtonClick()
}

interface OnExitIconClickListener {
    fun onExitIconClick()
}

class FilterBottomSheet(context: Context) : LinearLayout(context) {

    private val alcoholList: RecyclerView
    private val feelingList: RecyclerView
    private val resetButton: AppCompatButton
    private val exitIcon: ImageView
    private var listener: OnBottomSheetItemSelectListener? = null
    private var resetButtonClickListener: OnResetButtonClickListener? = null
    private var exitIconClickListener: OnExitIconClickListener? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    private val adjustButton: AppCompatButton
    private var selectedItemCount = 0

    private val alcoholAdapter : CustomAdapter
    private val feelingAdapter : CustomAdapter

    val alcoholListItems = mutableListOf(
        Triple("전체", false, "0"),
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

    val feelingListItems = mutableListOf(
        Triple("전체", false, "0"),
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

        val adapter = CustomAdapter(context, object : CustomAdapter.OnItemSelectedListener {
            override fun onItemSelected(itemType: String, position :Int, item: Triple<String, Boolean, String>) {
                TODO("Not yet implemented")
            }
        })

        // Create GridLayoutManager with 4 columns per row
        val layoutManager = GridLayoutManager(context, 4)

        // Set horizontal spacing between items to 8dp
        val spacing = resources.getDimensionPixelSize(R.dimen.item_spacing)
        val itemDecoration = GridSpacingItemDecoration(4, spacing, false)

        val alcoholLayoutManager = GridLayoutManager(context, 4)
        alcoholLayoutManager.orientation = GridLayoutManager.VERTICAL
        alcoholList = findViewById(R.id.alcohol_list)
        alcoholList.addItemDecoration(itemDecoration)
        // Set LayoutManager for RecyclerViews
        alcoholList.layoutManager = alcoholLayoutManager
        alcoholAdapter = CustomAdapter(context, object : CustomAdapter.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onItemSelected(itemType: String, position: Int, item: Triple<String, Boolean, String>) {
                //updateSelectedItems(alcoholListItems, item)
                //listener?.onItemSelected(itemType, Triple(item.first, item.second, item.third))
                alcoholListItems[position] = Triple(item.first, !item.second, item.third)
            }
        })
        alcoholList.adapter = alcoholAdapter
        alcoholList.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return rv.scrollState != RecyclerView.SCROLL_STATE_IDLE
            }
        })
        alcoholAdapter.setItems(alcoholListItems)

        val feelingLayoutManager = GridLayoutManager(context, 4)
        feelingLayoutManager.orientation = GridLayoutManager.VERTICAL
        feelingList = findViewById(R.id.feeling_list)
        feelingList.addItemDecoration(itemDecoration)
        feelingList.layoutManager = feelingLayoutManager
        feelingAdapter = CustomAdapter(context, object : CustomAdapter.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onItemSelected(itemType : String, position: Int, item: Triple<String, Boolean, String>) {
                updateSelectedItems(feelingListItems, item)
                listener?.onItemSelected(itemType, Triple(item.first, item.second, item.third))
            }
        })
        feelingList.adapter = feelingAdapter
        feelingList.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return rv.scrollState != RecyclerView.SCROLL_STATE_IDLE
            }
        })
        feelingAdapter.setItems(feelingListItems)

        resetButton.setOnClickListener {
            resetButtonClickListener?.onResetButtonClick()
        }

        exitIcon.setOnClickListener {
            exitIconClickListener?.onExitIconClick()
            bottomSheetDialog?.dismiss()
        }
    }

    fun show() {
        bottomSheetDialog = BottomSheetDialog(context, R.style.AppBottomSheetDialogTheme)
        bottomSheetDialog?.setContentView(this)
        bottomSheetDialog?.setCanceledOnTouchOutside(false)
        bottomSheetDialog?.show()
    }

    fun dismiss() {
        bottomSheetDialog?.dismiss()
    }

    fun updateFeelingAdapter() {
        feelingAdapter?.notifyDataSetChanged()
    }

    fun updateAlcoholAdapter() {
        alcoholAdapter?.notifyDataSetChanged()
    }

    fun setOnBottomSheetItemSelectListener(listener: OnBottomSheetItemSelectListener) {
        this.listener = listener
    }

    fun setOnResetButtonClickListener(listener: OnResetButtonClickListener) {
        resetButtonClickListener = listener
    }

    fun setOnExitIconClickListener(listener: OnExitIconClickListener) {
        exitIconClickListener = listener
    }

    fun toggleItemSelection(itemType: String, item: Triple<String, Boolean, String>) {
        when (itemType) {
            "feeling" -> {
                val index = feelingListItems.indexOf(item)
                if (index != -1) {
                    feelingListItems[index] = Triple(item.first, !item.second, item.third)
                }
            }
            "alcohol" -> {
                val index = alcoholListItems.indexOf(item)
                if (index != -1) {
                    alcoholListItems[index] = Triple(item.first, !item.second, item.third)
                }
                updateAlcoholAdapter()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateSelectedItems(itemList: MutableList<Triple<String, Boolean, String>>, selectedItem: Triple<String, Boolean, String>) {
        if (selectedItem.first == "전체") {
            val allSelected = !selectedItem.second
            itemList.replaceAll { item ->
                if (item.second != allSelected) {
                    if (allSelected) selectedItemCount++ else selectedItemCount--
                }
                Triple(item.first, allSelected, item.third)
            }
        } else {
            itemList.replaceAll { item ->
                if (item.third == selectedItem.third) {
                    if (item.second) selectedItemCount-- else selectedItemCount++
                    Triple(item.first, !item.second, item.third)
                } else {
                    item
                }
            }
        }
        updateResetButtonText()
        if (itemList == alcoholListItems) {
            alcoholAdapter.notifyDataSetChanged()
        } else {
            feelingAdapter.notifyDataSetChanged()
        }
    }

    private fun updateResetButtonText() {
        adjustButton.text = "필터 적용 ($selectedItemCount)"
    }

    class CustomAdapter(private val context: Context, private val listener: CustomAdapter.OnItemSelectedListener?) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        private val items = mutableListOf<Triple<String, Boolean, String>>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(context).inflate(R.layout.filter_item_layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
*/
/*
            val _item = Triple(item.first, !item.second, item.third)

            holder.itemView.setOnClickListener {
                listener?.onItemSelected("alcohol", _item)
            }*//*

            holder.bind("alcohol", position, item)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        fun setItems(newItems: List<Triple<String, Boolean, String>>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val itemName: TextView = itemView.findViewById(R.id.item_name)
            private val itemContainer: View = itemView.findViewById(R.id.filter_item_layout)

*/
/*            fun bind(itemType: String, triple: Triple<String, Boolean, String>) {
                itemName.text = triple.first
                if (triple.second) {
                    itemName.typeface = ResourcesCompat.getFont(context, R.font.pretendard_bold)
                    *//*
*/
/*itemName.setFont(ContextCompat.getColor(context, R.color.selected_text_color))*//*
*/
/*
                    itemContainer.setBackgroundResource(R.drawable.filter_selected_bg)
                } else {
                    *//*
*/
/*itemName.setTextColor(ContextCompat.getColor(context, R.color.unselected_text_color))*//*
*/
/*
                    itemName.typeface = ResourcesCompat.getFont(context, R.font.pretendard_regular)
                    itemContainer.setBackgroundResource(R.drawable.filter_unselected_bg)
                }
            }*//*

            fun bind(itemType: String, position : Int, triple: Triple<String, Boolean, String>) {
                itemName.text = triple.first
                if (triple.second) {
                    itemName.typeface = ResourcesCompat.getFont(context, R.font.pretendard_bold)
                    itemContainer.setBackgroundResource(R.drawable.filter_selected_bg)
                } else {
                    itemName.typeface = ResourcesCompat.getFont(context, R.font.pretendard_regular)
                    itemContainer.setBackgroundResource(R.drawable.filter_unselected_bg)
                }
                itemView.setOnClickListener {
                    listener?.onItemSelected(itemType, position, Triple(triple.first, !triple.second, triple.third))
                }
            }
        }

        interface OnItemSelectedListener {
            fun onItemSelected(itemType : String, position: Int, item: Triple<String, Boolean, String>)
        }
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

}*/

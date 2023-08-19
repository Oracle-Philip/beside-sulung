package com.rummy.sulung.view.ui.store

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.rummy.sulung.R
import com.rummy.sulung.common.App
import com.rummy.sulung.databinding.FragmentStoreBinding
import com.rummy.sulung.network.request.DiaryMonthlyRequest
import com.rummy.sulung.network.request.StoreListMonthlyRequest
import com.rummy.sulung.view.MainActivity
import com.rummy.sulung.view.ui.store.store_paging.PagingAndroidViewModel
import com.rummy.sulung.view.ui.store.store_paging.StoreAdapter
import com.rummy.sulung.viewmodel.diary.DiaryAndroidViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class StoreFragment : Fragment() {
    private val diaryAndroidViewModel : DiaryAndroidViewModel by viewModels{
        DiaryAndroidViewModel.Factory(App.instance)
    }
    private val viewModel : PagingAndroidViewModel by viewModels{
        PagingAndroidViewModel.Factory(App.instance)
    }

    private val sharedViewModel: SharedViewModel by viewModels()

    private var _binding: FragmentStoreBinding? = null
    private lateinit var mainActivity: MainActivity
    private val storeAdapter : StoreAdapter by lazy {
        StoreAdapter()
    }

    var drinkTypeValues = ""
    var emotionValues = ""

    var selectedYearMonth: String? = null

    var gridLayoutManager : GridLayoutManager? = null

    private var autoCompleteTextView: AutoCompleteTextView? = null

    /**
     * @date 0525 2023
     * @desc 횡단 이동시 초기화 하도록 멤버변수로 수정
     */
    var bottomSheet : FilterBottomSheet ?= null

    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        mainActivity.setStoreFragmentInstance(this)
    }

//    fun dpToPx(dp: Int): Int {
//        val density = Resources.getSystem().displayMetrics.density
//        return (dp * density).roundToInt()
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate<FragmentStoreBinding>(
            inflater, R.layout.fragment_store, container, false)

        //술창고 년월 값을 통해 스피너 엔트리 세팅
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadDiaryYearMonth()
        }

        autoCompleteTextView = binding.toolbar.autoCompleteTextView
        autoCompleteTextView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // 선택된 항목을 사용하여 월별 조회 등 필요한 동작 수행
            // 예: viewModel.loadDiaryMonthlyList(...)

            val yearMonth = parent?.getItemAtPosition(position) as String
            if (selectedYearMonth != yearMonth) {
                viewModel.setUsingLoadDiaryMonthlyList(date = yearMonth)
//                viewModel.loadDiaryMonthlyList(
//                    StoreListMonthlyRequest(
//                        drinkType = drinkTypeValues,
//                        emotion = emotionValues,
//                        date = yearMonth,
//                        size = 80
//                    )
//                )
            }
            selectedYearMonth = yearMonth
        }

        //스피너 엔트리 선택시 월별조회
        viewModel.diaryYearMonthResponse.observe(viewLifecycleOwner) { response ->
            response?.items?.let { yearMonthList ->
                val items = arrayOf("Option 1", "Option 2", "Option 3", "Option 30")

                val autoCompleteTextView: AutoCompleteTextView = binding.toolbar.autoCompleteTextView
                val adapter = ArrayAdapter(mainActivity, R.layout.dropdown_menu_popup_item, yearMonthList)
                (autoCompleteTextView as? AutoCompleteTextView)?.setAdapter(adapter)

                // 초기값 설정.
                try{
                    autoCompleteTextView.setText(yearMonthList[0], false)
                } catch (e: Exception){
                    Log.e("Exception", e.toString())
                }
            }
        }

        sharedViewModel.selectedFilters.observe(viewLifecycleOwner, { selectedFilters ->
            selectedFilters?.let {
                Log.e("selectedFilters", it.toString())

                if (it.isNotEmpty()) {
                    val drinkTypeIndex = it.indexOf("drinkType=")
                    val emotionIndex = it.indexOf("emotion=")
                    if (drinkTypeIndex >= 0) {
                        drinkTypeValues = if (emotionIndex != -1) {
                            it.substring(drinkTypeIndex + 10, emotionIndex).split(",").joinToString(separator = ",")
                        } else { it.substring(drinkTypeIndex + 10).split(",").joinToString(separator = ",") }
                    }
                    if (emotionIndex >= 0) {
                        emotionValues = it.substring(emotionIndex + 8).split(",").joinToString(separator = ",")
                    }
                }else{
                    //초기화
                    drinkTypeValues = ""
                    emotionValues = ""
                }
                viewModel.updateFilter(drinkType = drinkTypeValues, emotion = emotionValues)
            }
        })

        sharedViewModel.filterCount.observe(viewLifecycleOwner) { filterCount ->
            with(binding.toolbar) {
                filterCountLayout.visibility = if (filterCount != null && filterCount != 0) View.VISIBLE else View.GONE
                filterCount?.let { this.filterCount.text = it.toString() }
                filterDeleteBtn.visibility = if (filterCount != null && filterCount != 0) View.VISIBLE else View.GONE
            }
        }

        viewModel.regDiary.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { isSuccess ->
                if (isSuccess) {
                    viewModel.refresh()
                }
            }
        })

        viewModel.deleteDiary.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { isSuccess ->
                if (isSuccess) {
                    viewModel.refresh()
                }
            }
        })

        /**
         * @author ysp
         * @date 04 25 2023
         * @desc 술창고 월조회 통해 focus id로 이동하는 코드
         */
        viewModel.focusPosition.observe(viewLifecycleOwner) { position ->
            gridLayoutManager?.scrollToPositionWithOffset(position, 0)
        }

        val root: View = binding.root

        binding.lifecycleOwner = this@StoreFragment
        binding.viewModel = viewModel
        binding.storeList.setHasFixedSize(true)
        // LayoutManager를 수정합니다.

        val spanCount = 5
        gridLayoutManager = GridLayoutManager(context, 5, RecyclerView.VERTICAL, false)
        binding.storeList.layoutManager = gridLayoutManager
        binding.storeList.addItemDecoration(
            StoreItemSpacingDecoration(
                resources.getDimensionPixelSize(R.dimen.item_store_top_bottom_spacing),
                resources.getDimensionPixelSize(R.dimen.item_store_left_right_spacing)
            )
        )

        //divider 설정
        requireContext().getDrawable(R.drawable.img_store_item_divider)?.let {
            binding.storeList.addItemDecoration(DividerItemDecoration(it))
        }
        binding.storeList.adapter = storeAdapter

        /**
         * @author ysp
         * @date 04 27 2023
         * @desc 바텀쉽다이얼로그, filter_btn
         */
        with(binding.toolbar) {
            val displayMetrics = DisplayMetrics()
            mainActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val recyclerViewMargin = 20.dpToPx() * 2 // Assuming RecyclerView has 20dp margin on both sides
            val screenWidth = displayMetrics.widthPixels - recyclerViewMargin
            val spacing = 8.dpToPx() // dp to pixel conversion
            val itemCountPerRow = 4
            val totalSpacing = spacing * (itemCountPerRow - 1) // 각 행의 첫 번째와 마지막 아이템에 대한 간격을 제외한 총 간격
            val itemWidth = (screenWidth - totalSpacing) / itemCountPerRow

            bottomSheet = FilterBottomSheet(requireContext(), sharedViewModel, itemWidth)
            filterBtnLayout.setOnClickListener {
                bottomSheet?.setOnResetButtonClickListener(object :
                    FilterBottomSheet.OnResetButtonClickListener {
                    override fun onResetButtonClick() {

                    }
                })
                bottomSheet?.setOnAdjustButtonClickListener(object :
                    FilterBottomSheet.OnAdjustButtonClickListener {
                    override fun onAdjustButtonClick(select_filters: String) {
                        Log.e("select_filters", "onAdjustButtonClick: $select_filters")
                        bottomSheet?.dismiss()
                    }
                })
                bottomSheet?.setOnExitIconClickListener(object :
                    FilterBottomSheet.OnExitIconClickListener {
                    override fun onExitIconClick() {
                        bottomSheet?.dismiss()
                    }
                })
                bottomSheet?.show()
            }
            filterCount.text = ""
            filterDeleteBtn.setOnClickListener {
                bottomSheet?.resetSelections()
            }
        }
        return root
    }

    fun filteterReset(){
        bottomSheet?.resetSelections()
    }

    override fun onResume() {
        super.onResume()
        /*viewModel.refresh()*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }


    class CustomArrayAdapter(context: Context, private val resource: Int, private val items: List<String>) :
        ArrayAdapter<String>(context, resource, items) {

        var selectedPosition = 0

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return createView(position, convertView, parent)
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return createView(position, convertView, parent)
        }

        @SuppressLint("ResourceType")
        private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_dropdown_item, parent, false)

            val textView = view.findViewById<TextView>(R.id.text1)
            textView.text = getItem(position)

            val spinnerItemRoot = view.findViewById<LinearLayout>(R.id.spinner_item_root)
            val separatorTop = view.findViewById<View>(R.id.separatorTop)
            val separatorBottom = view.findViewById<View>(R.id.separatorBottom)

            // 선택된 항목에만 특정 배경을 적용
            if (position == selectedPosition) {
                val spinnerBackground = ContextCompat.getDrawable(context, R.drawable.spinner_background)
                spinnerItemRoot.background = spinnerBackground
                separatorTop.visibility = View.GONE
                separatorBottom.visibility = View.GONE
            } else {
                val spinnerItemBackground = ContextCompat.getDrawable(context, R.drawable.spinner_item_background)
                spinnerItemRoot.background = spinnerItemBackground
                separatorTop.visibility = View.VISIBLE
                separatorBottom.visibility = View.VISIBLE
            }

            return view
        }
    }

    class StoreItemSpacingDecoration(private val top_bottom_spaceSize: Int, private val left_right_spaceSize: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.top = top_bottom_spaceSize
            outRect.bottom = top_bottom_spaceSize
            outRect.left = left_right_spaceSize
            outRect.right = left_right_spaceSize
        }
    }

    class DividerItemDecoration(private val divider: Drawable) : RecyclerView.ItemDecoration() {
        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            val left = parent.paddingLeft
            val right = parent.width - parent.paddingRight

            val childCount = parent.childCount
            for (i in 0 until childCount) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams

                val top = child.bottom + params.bottomMargin - 52
                val bottom = top + divider.intrinsicHeight

                if ((i + 1) % 5 == 0 || i == childCount - 1) { // 마지막 아이템인 경우도 처리
                    divider.setBounds(left, top, right, bottom)
                    divider.draw(c)
                }
            }
        }
    }
}
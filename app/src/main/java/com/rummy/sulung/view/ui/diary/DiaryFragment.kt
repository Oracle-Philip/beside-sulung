package com.rummy.sulung.view.ui.diary

import android.app.Activity.RESULT_OK
import android.content.Context
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.rummy.sulung.R
import com.rummy.sulung.common.App
import com.rummy.sulung.databinding.FragmentDiaryBinding
import com.rummy.sulung.network.request.DiaryMonthlyRequest
import com.rummy.sulung.network.request.StoreListMonthlyRequest
import com.rummy.sulung.view.MainActivity
import com.rummy.sulung.view.ui.diary.paging_another.PagingAndroidViewModel
import com.rummy.sulung.viewmodel.diary.DiaryAndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.lang.Exception

class DiaryFragment : Fragment() {

    var launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {

        }
    }

    val diaryAdapter : DiaryAdapter by lazy {
        DiaryAdapter()
    }

    val diaryAndroidViewModel : DiaryAndroidViewModel by lazy{
        ViewModelProvider(mainActivity, DiaryAndroidViewModel.Factory(App.instance))
            .get(DiaryAndroidViewModel::class.java)
    }
    val viewModel : PagingAndroidViewModel by lazy {
        ViewModelProvider(mainActivity, PagingAndroidViewModel.Factory(App.instance))
            .get(PagingAndroidViewModel::class.java)
    }
    lateinit var body : MultipartBody.Part
    lateinit var mainActivity: MainActivity
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    private var gridLayoutManager: GridLayoutManager? = null

    private var autoCompleteTextView: AutoCompleteTextView? = null

    var sort = "NEWEST"

    var selectedYearMonth: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate<FragmentDiaryBinding>(
            inflater, R.layout.fragment_diary, container, false
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadDiaryYearMonth()
        }

        autoCompleteTextView = binding.toolbar.autoCompleteTextView
        autoCompleteTextView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // 선택된 항목을 사용하여 월별 조회 등 필요한 동작 수행
            // 예: viewModel.loadDiaryMonthlyList(...)

            val yearMonth = parent?.getItemAtPosition(position) as String
            if (selectedYearMonth != yearMonth) {
                viewModel.loadDiaryMonthlyList(DiaryMonthlyRequest(date = yearMonth, size = 80, sort = sort))
            }
            selectedYearMonth = yearMonth
        }

        viewModel.diaryYearMonthResponse.observe(viewLifecycleOwner) {
            it?.items?.let { yearMonthList ->
                /*val adapter = ArrayAdapter(mainActivity, R.layout.spinner_item, yearMonthList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.toolbar.yearMonthSpinner.adapter = adapter*/

                val items = arrayOf("Option 1", "Option 2", "Option 3", "Option 30")

                val adapter = ArrayAdapter(mainActivity, R.layout.dropdown_menu_popup_item, yearMonthList)
                (autoCompleteTextView as? AutoCompleteTextView)?.setAdapter(adapter)

                // 초기값 설정.
                try{
                    //qa 0529
                    //java.lang.IndexOutOfBoundsException: Empty list doesn't contain element at index 0.
                    autoCompleteTextView?.setText(yearMonthList[0], false)
                } catch (e: Exception){
                    Log.e("DiaryFragment", "Error: $e")
                }
            }
        }

        viewModel.sort.observe(viewLifecycleOwner) { sortOrder ->
            sort = sortOrder
            binding.toolbar.newestOldestText.text = if (sortOrder == "NEWEST") "최신순" else "오래된순"
        }

        /**
         * @author ysp
         * @date 04 30 2023
         * @desc 다이어리 월별조회 통해 focus id로 이동하는 코드
         */
        viewModel.focusPosition.observe(viewLifecycleOwner) { position ->
            val item = diaryAdapter.currentList?.get(position)
            Log.e("DiaryFragment", "Position: $position, Item: $item")

            val smoothScroller = object : LinearSmoothScroller(context) {
                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_START
                }
                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    return super.calculateSpeedPerPixel(displayMetrics) / 3
                }
            }
            smoothScroller.targetPosition = position
            gridLayoutManager?.startSmoothScroll(smoothScroller)
        }

        with(binding.toolbar) {
            newestOldest.setOnClickListener {
                viewModel.updateSortOrder()
            }

        binding.lifecycleOwner = this@DiaryFragment
        binding.viewModel = viewModel

        gridLayoutManager = GridLayoutManager(context, 1, RecyclerView.VERTICAL, false)
        binding.diaryList.layoutManager = gridLayoutManager

        val customScrollListener = CustomScrollListener()

        binding.diaryList.addOnScrollListener(customScrollListener)

        binding.diaryList.setHasFixedSize(true)
    }
        binding.diaryList.adapter = diaryAdapter

        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class CustomScrollListener : RecyclerView.OnScrollListener() {

        private var firstVisibleItemPosition: Int = 0

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            // 현재 보이는 첫번째 아이템의 포지션을 가져옵니다.
            firstVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        }

        fun getFirstVisibleItemPosition() = firstVisibleItemPosition
    }

}
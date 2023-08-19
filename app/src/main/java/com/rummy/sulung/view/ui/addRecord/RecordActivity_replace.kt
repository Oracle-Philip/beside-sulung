package com.rummy.sulung.view.ui.addRecord

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rummy.sulung.R
import com.rummy.sulung.common.App
import com.rummy.sulung.databinding.ActivityRecordReplaceBinding
import com.rummy.sulung.view.EmptyDataMainActivity
import com.rummy.sulung.viewmodel.diary.DiaryAndroidViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

data class Item(val imageResId: Int, val name: String, var isSelected: Boolean = false)

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

class RecordActivity_replace : AppCompatActivity() {

    val TAG = RecordActivity_replace::class.java.simpleName

    lateinit var toolbar: Toolbar
    lateinit var binding : ActivityRecordReplaceBinding

    var drinkType = -100
    var emotion = -100
    var selectdate : Long = System.currentTimeMillis()

    var isFirstRecord = false

    companion object {
        const val FIRST_RECORD = "first_record"
    }

    val alchohol_list = mutableListOf(
        Quadruple(R.drawable.sel_soju, "소주", false, 0),
        Quadruple(R.drawable.selbeer, "맥주", false, 1),
        Quadruple(R.drawable.sel_makgeolli, "막걸리", false, 2),

        Quadruple(R.drawable.sel_cocktail, "칵테일", false, 4),
        Quadruple(R.drawable.sel_sake, "사케", false, 6),
        Quadruple(R.drawable.sel_highball, "하이볼", false, 8),

        Quadruple(R.drawable.sel_champagne, "샴페인", false, 7),
        Quadruple(R.drawable.sel_liquor, "양주", false, 3),
        Quadruple(R.drawable.sel_wine, "와인", false, 5),
    )

    val emotion_list = mutableListOf(
        Quadruple(R.drawable.sel_tranquility, "평온", false, 4),
        Quadruple(R.drawable.sel_depressed, "우울", false, 2),
        Quadruple(R.drawable.sel_aggro, "화남", false, 3),
        Quadruple(R.drawable.sel_sadness, "슬픔", false, 1),
        Quadruple(R.drawable.sel_pleasure, "기쁨", false, 0),
        Quadruple(R.drawable.sel_congrats, "축하", false, 5),
        Quadruple(R.drawable.sel_intoxication, "취함", false, 6)
    )

    val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        result ->
        run {
            if (result.resultCode == Activity.RESULT_OK) {
                val resultData = result.data?.getIntExtra("result", 0)
                Log.e(TAG, resultData.toString())
                selectdate = System.currentTimeMillis()
                binding.recordSub.dateInput.setText(SimpleDateFormat("yyyy년 M월 d일").format(System.currentTimeMillis()).toString())
                drinkType = -100
                emotion = -100
            }
        }
    }

    val diaryAndroidViewModel : DiaryAndroidViewModel by lazy{
        ViewModelProvider(this, DiaryAndroidViewModel.Factory(App.instance))
            .get(DiaryAndroidViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordReplaceBinding.inflate(layoutInflater)

        /*val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val recyclerViewMargin = dpToPx(20) * 2 // Assuming RecyclerView has 20dp margin on both sides
        val screenWidth = displayMetrics.widthPixels - recyclerViewMargin
        val spacing = dpToPx(8) // dp to pixel conversion
        val itemCountPerRow = 3
        val totalSpacing = spacing * (itemCountPerRow - 1) // 각 행의 첫 번째와 마지막 아이템에 대한 간격을 제외한 총 간격
        val itemWidth = (screenWidth - totalSpacing) / itemCountPerRow*/
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val recyclerViewMargin = dpToPx(20) * 2 // Assuming RecyclerView has 20dp margin on both sides
        val screenWidth = displayMetrics.widthPixels - recyclerViewMargin
        val spaceSize = dpToPx(8).toFloat()
        val itemCountPerRow = 3

        val totalSpacing = spaceSize * (itemCountPerRow - 1)
        val itemWidthFloat = (screenWidth - totalSpacing) / itemCountPerRow
        val itemWidth = itemWidthFloat.roundToInt()

/*        val isScreenWidthVerified = verifyScreenWidth(screenWidth, itemWidth, itemCountPerRow, spacing.toInt())
        Log.i("WidthVerification", "Is screen width verified: $isScreenWidthVerified")*/
        //val isScreenWidthVerified = verifyScreenWidth(displayMetrics, itemWidth, itemCountPerRow, spacing.toInt(), 20)
        //Log.i("WidthVerification", "Is screen width verified: $isScreenWidthVerified")

        /*        // Distribute the remainder pixels to the spacing
                val remainder = screenWidth - totalSpacing - itemWidth * itemCountPerRow
                //val extraSpacing = remainder / (itemCountPerRow - 1)
                val extraSpacing = Math.round(remainder.toFloat() / (itemCountPerRow - 1))*/



        val intent : Intent by lazy {
            getIntent()
        }

        isFirstRecord = intent.getBooleanExtra(FIRST_RECORD, false)

        binding.apply {
            with(toolbar){
                toolbarTitle.text = getString(R.string.record_toolbar_title)
                exitIcon.setOnClickListener {
                    if(isFirstRecord){
                        val intent = Intent(this@RecordActivity_replace, EmptyDataMainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    else
                        finish()
                }
            }
        }

        with(binding.recordSub){
            val recyclerView = alcoholList
            recyclerView.layoutManager = GridLayoutManager(this@RecordActivity_replace, 3, GridLayoutManager.VERTICAL, false)
            recyclerView.addItemDecoration(ItemSpacingDecoration(
                dpToPx(8).toFloat(), spanCount = 3
            ))
            recyclerView.adapter = Alcohol_Image_Adapter(alchohol_list, itemWidth).apply {
                setOnItemClickListener(object : Alcohol_Image_Adapter.OnItemClickListener {
                    override fun onItemClick(value: Int) {
                        drinkType = value // 클릭한 아이템의 포지션을 drinkType 변수에 저장
                    }
                })
            }
            recyclerView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    return rv.scrollState != RecyclerView.SCROLL_STATE_IDLE
                }
            })

            val emotion_recyclerView = emotionList
            emotion_recyclerView.layoutManager = GridLayoutManager(this@RecordActivity_replace, 3, GridLayoutManager.VERTICAL, false)
            emotion_recyclerView.addItemDecoration(ItemSpacingDecoration(
                dpToPx(8).toFloat(), 3
            ))
            emotion_recyclerView.adapter = Emotion_Image_Adapter(emotion_list, itemWidth).apply {
                setOnItemClickListener(object : Emotion_Image_Adapter.OnItemClickListener {
                    override fun onItemClick(value: Int) {
                        emotion = value // 클릭한 아이템의 포지션을 drinkType 변수에 저장
                    }
                })
            }
            emotion_recyclerView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    return rv.scrollState != RecyclerView.SCROLL_STATE_IDLE
                }
            })

            dateInput.setText(SimpleDateFormat("yyyy년 M월 d일").format(System.currentTimeMillis()).toString())

            selectDate.setEndIconOnClickListener{
                val today = GregorianCalendar()
                val year: Int = today.get(Calendar.YEAR)
                val month: Int = today.get(Calendar.MONTH)
                val date: Int = today.get(Calendar.DATE)
                val dlg = DatePickerDialog(this@RecordActivity_replace, object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                        dateInput.setText("${year}년 ${month+1}월 ${dayOfMonth}일")
                        selectdate = GregorianCalendar(year, month, dayOfMonth).timeInMillis
                    }
                }, year, month, date)
                dlg.show()
            }

            regEasilyBtn.setOnClickListener {
//                간단 등록
                lifecycleScope.launch {
                    //{"id":203 204, 205, 206}
                    if(drinkType != -100 && emotion != -100) {
                        diaryAndroidViewModel.regDiaryEasily(
                            diaryDt = selectdate,
                            drinkType = drinkType,
                            emotion = emotion
                        )
                        val intent = Intent(this@RecordActivity_replace, EasilyRegToDiary::class.java)
                        intent.putExtra(EasilyRegToDiary.DRINKTYPE, drinkType)
                        intent.putExtra(EasilyRegToDiary.EMOTION, emotion)
                        intent.putExtra(EasilyRegToDiary.DIARYDT, selectdate)
                        intent.putExtra(EasilyRegToDiary.FIRST_RECORD, isFirstRecord)
                        //intent.putExtra(EasilyRegToDiary.ID, id)
                        requestLauncher.launch(intent)
                        finish()
                    }
                    else
                        Toast.makeText(this@RecordActivity_replace, "술과 감정을 선택해 주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        supportActionBar?.setDisplayShowTitleEnabled(false)
        setContentView(binding.root)
    }

    fun dpToPx(dp: Int): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (dp * density).roundToInt()
    }

    class ItemSpacingDecoration(private val spaceSize: Float, private val spanCount: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)

            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount

            val horizontalSpacing = spaceSize / (spanCount - 1)

            if (column == 0) {
                outRect.left = 0
                outRect.right = (horizontalSpacing).roundToInt()
            } else if (column == spanCount - 1) {
                outRect.left = (horizontalSpacing).roundToInt()
                outRect.right = 0
            } else {
                outRect.left = (horizontalSpacing / 2).roundToInt()
                outRect.right = (horizontalSpacing / 2).roundToInt()
            }

            if (position >= spanCount) {
                outRect.top = spaceSize.roundToInt()
            }
        }
    }

    fun verifyScreenWidth(screenWidth: Int, itemWidth: Int, itemCountPerRow: Int, spacing: Int): Boolean {
        // Calculate total space occupied by spacing and item widths
        val totalSpaceOccupied = (itemWidth * itemCountPerRow) + ((itemCountPerRow - 1) * spacing)
        return screenWidth == totalSpaceOccupied
    }

    fun verifyScreenWidth(displayMetrics: DisplayMetrics, itemWidth: Int, itemCountPerRow: Int, spacing: Int, marginInDp: Int): Boolean {
        // Calculate total space occupied by spacing, item widths, and RecyclerView margins
        val recyclerViewMargin = dpToPx(marginInDp) * 2
        val totalSpaceOccupied = (itemWidth * itemCountPerRow) + ((itemCountPerRow - 1) * spacing) + recyclerViewMargin
        return displayMetrics.widthPixels == totalSpaceOccupied
    }
/*
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    }*/



    /*class ItemSpacingDecoration(private val spaceSize: Float, private val spanCount: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)

            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount

            val leftSpacing = column * (spaceSize / (spanCount - 1))
            val rightSpacing = spaceSize - leftSpacing

            if (column == 0) {
                outRect.left = 0
            } else {
                outRect.left = leftSpacing.toInt()
            }

            if (column == spanCount - 1) {
                outRect.right = 0
            } else {
                outRect.right = rightSpacing.toInt()
            }

            // If not the first row
            if (position >= spanCount) {
                outRect.top = spaceSize.roundToInt()
            }
        }
    }*/
}
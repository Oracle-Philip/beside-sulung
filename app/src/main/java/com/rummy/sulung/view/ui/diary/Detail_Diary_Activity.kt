package com.rummy.sulung.view.ui.diary

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayoutMediator
import com.rummy.sulung.R
import com.rummy.sulung.common.DiaryImage
import com.rummy.sulung.common.Prefs
import com.rummy.sulung.databinding.ActivityDetailDiaryBinding
import com.rummy.sulung.network.response.ReadDiaryDetailResponse
import com.rummy.sulung.network.userInfo.UserInfoResponse
import com.rummy.sulung.network.userInfo.UserInfoViewModel
import com.rummy.sulung.view.EmptyDataMainActivity
import com.rummy.sulung.view.LoginActivity
import com.rummy.sulung.view.MainActivity
import com.rummy.sulung.view.ui.diary.ModifyDiaryActivity.Companion.DIARY_INFO
import com.rummy.sulung.view.ui.diary.WritingDiaryActivity.Companion.DIARYDT
import com.rummy.sulung.view.ui.diary.WritingDiaryActivity.Companion.DRINKTYPE
import com.rummy.sulung.view.ui.diary.WritingDiaryActivity.Companion.EMOTION
import com.rummy.sulung.view.ui.diary.WritingDiaryActivity.Companion.HASH_TAG
import com.rummy.sulung.view.ui.diary.WritingDiaryActivity.Companion.ID
import com.rummy.sulung.viewmodel.diary.DiaryAndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class Detail_Diary_Activity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var binding : ActivityDetailDiaryBinding
    lateinit var tooltipView: View

    val TAG = Detail_Diary_Activity::class.simpleName

    val recordId by lazy {
        intent.getIntExtra(RECORD_ID, -1)
    }

    var diaryInfo : ReadDiaryDetailResponse? = null

    val diaryAndroidViewModel : DiaryAndroidViewModel by viewModels{
        DiaryAndroidViewModel.Factory(application)
    }

    /**
     * qa0529
     * 삭제시 모든 일기 지우면
     * 우선 사용자 정보 통해 다이어리 갯수 파악
     */
    var userInfo : UserInfoResponse? = null

    val userInfoViewModel: UserInfoViewModel by viewModels()

    companion object {
        const val RECORD_ID = "record_id"
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailDiaryBinding.inflate(layoutInflater)
        diaryAndroidViewModel.deleteDiary.observe(this@Detail_Diary_Activity, Observer {
            if(it){
                lifecycleScope.launch(Dispatchers.IO){
                    val response = userInfoViewModel.UserInfo()
                    val result = response?.body()

                    /**
                     * qa0529 반영 모든 술 삭제시
                     */
                    if(result?.diaryCount!! > 0){
                        withContext(Dispatchers.Main){
                            val intent = Intent(this@Detail_Diary_Activity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }else{
                        withContext(Dispatchers.Main){
                            val intent = Intent(this@Detail_Diary_Activity, EmptyDataMainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
                }
            }
        })
        diaryAndroidViewModel.diaryDetailInfo.observe(this@Detail_Diary_Activity, Observer<ReadDiaryDetailResponse> {
            if(it != null){
                diaryInfo = it

                with(binding){
                    //summary
                    summary.text =
                        "${resources.getStringArray(R.array.emotion_array)[diaryInfo?.emotion ?: 0]} 기분으로 " +
                        "${resources.getStringArray(R.array.drink_array)[diaryInfo?.drink?.type ?: 0]} " +
                        "마셨어요!"

                        // 타임스탬프 값을 날짜 문자열로 변환
                        val timestamp = diaryInfo?.diaryDt ?: 0L
                        val date = Date(timestamp)
                        val format = SimpleDateFormat("M월 d일 EEEE")
                        val dateString = format.format(date)
                        toolbar.toolbarDateTitle.text = dateString
                        toolbar.toolbarBack.setOnClickListener {
                            onBackPressed()
                        }
                        toolbar.editDelete.setOnClickListener {
                            tooltipView = android.view.LayoutInflater.from(this@Detail_Diary_Activity).inflate(com.rummy.sulung.R.layout.tooltip_layout, null)

                            // PopupWindow 객체를 생성합니다.
                            val popupWindow = PopupWindow(tooltipView, TooltipDpToPx(110), TooltipDpToPx(96))

                            // 툴팁 뷰 내부의 TextView를 찾아 클릭 이벤트를 설정합니다.
                            val modifyTextView = tooltipView.findViewById<TextView>(R.id.modifyTextView)
                            val deleteTextView = tooltipView.findViewById<TextView>(R.id.deleteTextView)

                            modifyTextView.setOnClickListener {
                                // "수정" TextView가 클릭되었을 때 발생하는 이벤트 처리
                                //Toast.makeText(this@Detail_Diary_Activity, "수정", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@Detail_Diary_Activity, ModifyDiaryActivity::class.java)
                                intent.putExtra(DIARY_INFO, diaryInfo)
                                startActivity(intent)
                                popupWindow.dismiss() // 툴팁을 닫습니다.
                            }

                            deleteTextView.setOnClickListener {
                                showPopup()
                                /*lifecycleScope.launch(Dispatchers.IO){
                                    diaryAndroidViewModel.deleteDiary(recordId)
                                    withContext(Dispatchers.Main){
                                        val resultIntent = Intent()
                                        setResult(Activity.RESULT_OK, resultIntent)
                                        finish()
                                    }
                                }*/
                                popupWindow.dismiss() // 툴팁을 닫습니다.
                            }

                            // 툴팁을 보여줍니다.
                            popupWindow.showAsDropDown(it, 32, 8, Gravity.END)
                        }


                        when(diaryInfo?.emotion){
                            0 -> diaryDetailView.background = getDrawable(R.drawable.bg_emotion_happy)
                            1 -> diaryDetailView.background = getDrawable(R.drawable.bg_emotion_sad)
                            2 -> diaryDetailView.background = getDrawable(R.drawable.bg_emotion_gloom)
                            3 -> diaryDetailView.background = getDrawable(R.drawable.bg_emotion_angry)
                            4 -> diaryDetailView.background = getDrawable(R.drawable.bg_emotion_calm)
                            5 -> diaryDetailView.background = getDrawable(R.drawable.bg_emotion_congratulate)
                            6 -> diaryDetailView.background = getDrawable(R.drawable.bg_emotion_drunk)
                        }
                        val (emotion, drinkType) = Pair(diaryInfo?.emotion, diaryInfo?.drink?.type)
                        if(emotion != null && drinkType != null)
                            alcoholImg.setImageResource(DiaryImage.setDrinkImg(emotion = emotion!!, drinkType = drinkType!!))

                    //태그
                    /**
                     * @desc qa0522 반영
                     */
                    binding.chipGroup.removeAllViews()
                    binding.emptyDiaryChipGroup.removeAllViews()
                    val tag = diaryInfo?.tag
                    val list = tag?.split(",")?.distinct()?.toList()
                    list?.forEach { item ->
                        if (item.isNotBlank()) {
                            addChipToGroup(item, diaryInfo?.emotion!!, it.createdAt != it.updatedAt)
                        }
                    }

                    alcoholName.visibility = View.GONE
                    alcoholCount.visibility = View.GONE
                    divider.visibility = View.GONE
                    diaryContent.visibility = View.GONE

                    if(it.createdAt != it.updatedAt){

                        //술 일기 쓰기 버튼 숨기기
                        writingDiaryBtn.visibility = View.GONE

                        //다이어리 상세뷰 보이기
                        detailDiary.visibility = View.VISIBLE

                        //음주량, 음주단위, 음주이름
                        alcoholName.visibility = View.VISIBLE
                        alcoholCount.visibility = View.VISIBLE
                        val count = diaryInfo?.drink?.count
                        drinkCount.text = if (count != null) { String.format(if (count % 1 == 0.0) "%.0f" else "%.1f", count) } else { "" }
                        drinkUnit.text = diaryInfo?.drink?.unit


                        //drinkName.text = diaryInfo?.drink?.name
                        val name = diaryInfo?.drink?.name
                        if (name == null || name.equals("null", ignoreCase = true) || name.isEmpty()) {
                            drinkName.setText("")
                        } else {
                            drinkName.setText(name ?: "")
                        }

                        divider.visibility = View.VISIBLE

                        //내용
                        diaryContent.visibility = View.VISIBLE
                        val content = diaryInfo?.content
                        binding.diaryContent.text = diaryInfo?.content
                        if (content == null || content.equals("null", ignoreCase = true) || content.isEmpty()) {
                            diaryContent.setText("")
                        } else {
                            diaryContent.setText(content ?: "")
                        }

                        //이미지 뷰페이저
                        val images = diaryInfo?.imageUrls
                        if (images != null) {
                            val adapter = Detail_Diary_ViewPager(this@Detail_Diary_Activity, images as List<String>)
                            viewPager.adapter = adapter
                        } else {
                            // images가 null일 때
                        }

                        // 인디케이터
                        // 이미지 하나일때 인디케이터 미표시  -- 05 02 2023
                        binding.indicatorLayout.removeAllViews()
                        val indicatorLayout = binding.indicatorLayout
                        val indicators = mutableListOf<View>()

                        if ((images?.size ?: 0) > 1) {
                            images?.forEachIndexed { index, imageUrl ->
                                val indicator = View(this@Detail_Diary_Activity).apply {
                                    id = View.generateViewId()
                                    layoutParams = LinearLayout.LayoutParams(dpToPx(8), dpToPx(8)).apply {
                                        if (index > 0) {
                                            marginStart = dpToPx(8)
                                        }
                                    }
                                    setBackgroundResource(R.drawable.indicator_unselected)
                                }
                                indicators.add(indicator)
                                indicatorLayout.addView(indicator)
                            }

                            // 인디케이터 선택
                            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                                override fun onPageSelected(position: Int) {
                                    super.onPageSelected(position)
                                    for (i in indicators.indices) {
                                        if (i == position) {
                                            indicators[i].setBackgroundResource(R.drawable.indicator_selected)
                                        } else {
                                            indicators[i].setBackgroundResource(R.drawable.indicator_unselected)
                                        }
                                    }
                                }
                            })
                        }

                    } else{
                        //다이어리 등록버튼 보이기
                        writingDiaryBtn.visibility = View.VISIBLE

                        //다이어리 상세뷰 보이기
                        detailDiary.visibility = View.GONE

                        writingDiaryBtn.setOnClickListener {
                            val intent = Intent(this@Detail_Diary_Activity, WritingDiaryActivity::class.java)
                            intent.putExtra(ID, diaryInfo?.id ?: 0)
                            intent.putExtra(DIARYDT, diaryInfo?.diaryDt ?: 0L)
                            intent.putExtra(DRINKTYPE, diaryInfo?.drink?.type ?: 0)
                            intent.putExtra(EMOTION, diaryInfo?.emotion ?: 0)
                            intent.putExtra(HASH_TAG, diaryInfo?.tag ?: "")
                            startActivity(intent)
                        }
                    }
                }
            }
        })
        supportActionBar?.setDisplayShowTitleEnabled(false)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            diaryAndroidViewModel.getDiaryDetailInfo(recordId)
        }
    }

    private fun addChipToGroup(tag: String, emotion: Int, isUpdate: Boolean) {
        tag?.let{
            val chip = createChip(tag, emotion)
            if(isUpdate) binding.chipGroup.addView(chip)
            else binding.emptyDiaryChipGroup.addView(chip)
        }
    }

    fun Context.dpToPx(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    fun TooltipDpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    private fun createChip(tag: String, emotion: Int): Chip {
        val chip = LayoutInflater.from(this).inflate(R.layout.chip_item_detail_diary, binding.chipGroup, false) as Chip
        chip.minWidth = 0
        chip.text = "#$tag"
        chip.setTextColor(ContextCompat.getColor(this, when (emotion) {
            0 -> R.color.happy_primary
            1 -> R.color.sad_primary
            2 -> R.color.gloom_primary
            3 -> R.color.angry_primary
            4 -> R.color.calm_primary
            5 -> R.color.congratulate_primary
            6 -> R.color.drunk_primary
            else -> R.color.happy_primary // 기본값
        }))
        chip.setChipBackgroundColorResource(when (emotion) {
            0 -> R.color.happy_light
            1 -> R.color.sad_light
            2 -> R.color.gloom_light
            3 -> R.color.angry_light
            4 -> R.color.calm_light
            5 -> R.color.congratulate_light
            6 -> R.color.drunk_light
            else -> R.color.happy_light // 기본값
        })
        return chip
    }

    private fun showPopup() {
        val popupView = LayoutInflater.from(this).inflate(R.layout.diary_delete_popup, null)
        val popupWindow = PopupWindow(popupView, 300.dpToPx(), 200.dpToPx())

        // 팝업의 배경 색상과 외곽선 설정
        popupWindow.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_rounded_border3, null))
        popupWindow.elevation = 10F

        // 팝업의 크기와 위치를 조정
        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)

        // 팝업 외부 영역 터치 불가능 설정
        popupWindow.setOutsideTouchable(false)

        // 팝업 뷰 내부의 위젯에 이벤트 핸들러 등록
        with(popupView){
            findViewById<ImageView>(R.id.exit).setOnClickListener {
                popupWindow.dismiss()
            }
            findViewById<Button>(R.id.cancel).setOnClickListener {
                popupWindow.dismiss()
            }
            findViewById<Button>(R.id.delete).setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO){
                    diaryAndroidViewModel.deleteDiary(recordId)
                    /**
                     * qa0529 반영 모든 술 삭제시
                     */
                    withContext(Dispatchers.Main){
                        popupWindow.dismiss()
                    }
                }
            }
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }
}
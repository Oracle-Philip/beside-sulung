package com.rummy.sulung.view.ui.diary

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputLayout
import com.rummy.sulung.R
import com.rummy.sulung.common.App
import com.rummy.sulung.common.DiaryTitle
import com.rummy.sulung.databinding.ActivityDiaryBinding
import com.rummy.sulung.view.MainActivity
import com.rummy.sulung.view.ui.addRecord.ImageAdapter
import com.rummy.sulung.viewmodel.diary.DiaryAndroidViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class WritingDiaryActivity : DiaryBaseActivity_WritingDiary(){

    val TAG = WritingDiaryActivity::class.java.simpleName

    lateinit var toolbar: Toolbar
    lateinit var binding : ActivityDiaryBinding

    companion object{
        const val DIARYDT = "diaryDt"
        const val ID = "id"
        const val DRINKTYPE = "drink_type"
        const val EMOTION = "emotion"
        const val FIRST_RECORD = "first_record"
        const val HASH_TAG = "hashtag"
    }

    val diaryAndroidViewModel : DiaryAndroidViewModel by viewModels{
        DiaryAndroidViewModel.Factory(App.instance)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent : Intent by lazy {
            getIntent()
        }

        diaryAndroidViewModel.regDiary.observe(this, androidx.lifecycle.Observer<Boolean> {
            if(it){
                binding.progressBar.visibility = View.GONE
                showPopup(binding.root)
            }else{
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "일기 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })

        diaryDt = intent.getLongExtra(DIARYDT, -100L)
        id = intent.getIntExtra(ID, -100)
        drinkType = intent.getIntExtra(DRINKTYPE, -100)
        emotion = intent.getIntExtra(EMOTION, -100)
        isFirstRecord = intent.getBooleanExtra(FIRST_RECORD, false)
        hashTag = intent.getStringExtra(HASH_TAG) ?: ""

        binding = ActivityDiaryBinding.inflate(layoutInflater)

        binding.parentLayout.apply {
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
            postDelayed({
                descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
            }, 300)
        }

        toolbar = binding.toolbar.root

        binding.apply {
            with(toolbar){
                toolbarTitle.text = getString(R.string.record_toolbar_title)
                exitIcon.setOnClickListener {
                    if(!isFirstRecord)
                        finish()
                    else{
                        val intent = Intent(this@WritingDiaryActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            }
        }

        supportActionBar?.setDisplayShowTitleEnabled(false)
        setContentView(binding.root)

        with(binding.recordSub){
            imageAdapter = ImageAdapter(mutableListOf())

            val layoutManager = GridLayoutManager(this@WritingDiaryActivity, 3)
            imageRecyclerView.layoutManager = layoutManager
            imageRecyclerView.adapter = imageAdapter
            imageRecyclerView.isNestedScrollingEnabled = false // 스크롤 기능 비활성화
            imageRecyclerView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    return rv.scrollState != RecyclerView.SCROLL_STATE_IDLE
                }
            })

            //diaryTitle.setMarginTopBottomInDp(40, 32)
            diaryTitle.setText("${SimpleDateFormat("yyyy년 M월 d일").format(diaryDt)}\n${DiaryTitle.setDiaryTitle(emotion, drinkType)}")

            hashtagLayout.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    hashtagInput.requestFocus()
                    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(binding.recordSub.hashtagInput, InputMethodManager.SHOW_IMPLICIT)
                }
            }
            hashtagInput.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                    createHashtags()
                    hashtagInput.clearFocus()
                    val minimumWidthInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 68f, hashtagInput.resources.displayMetrics).toInt()
                    val layoutParams = hashtagInput.layoutParams
                    layoutParams.width = minimumWidthInPixels
                    hashtagInput.layoutParams = layoutParams
                }
                false
            }
            imageAdapter.setOnImageDeleteListener(object : ImageAdapter.OnImageDeleteListener {
                override fun onImageDeleted(position: Int) {
                    imageAdapter.deleteImage(position)
                    imageBody.removeAt(position)
                }
            })
            imageAdapter.setOnImageAddListener { position ->
                selectedPosition = position
                requestPermissions()
//                val chooserIntent = createImageSelectionOrCameraIntent()
//                launcher.launch(chooserIntent)
            }

            //다이어리제목, 다이어리 이름 인풋필드에서 바깥을 터치시 키보드 숨기기
            //drinkNameTitle.setMarginTopBottomInDp(48, 12)
            binding.parentLayout.setOnTouchListener { v, event ->
                memoInput.clearFocus()
                drinkNameInput.clearFocus()
                drinkCountInput.clearFocus()
                hashtagInput.clearFocus()
                false
            }
            subParentLayout.setOnTouchListener { v, event ->
                memoInput.clearFocus()
                drinkNameInput.clearFocus()
                drinkCountInput.clearFocus()
                hashtagInput.clearFocus()
                false
            }

            //이름이 기억나지 않아요 -- 체크박스
            drinkNameInput.setText("")
            drinkNameInput.isEnabled = false
            checkboxDrinkName.isChecked = true
            checkboxDrinkName.setOnCheckedChangeListener { buttonView, isChecked ->
                drinkNameInput.isEnabled = !isChecked
                if(isChecked) {
                    // 체크박스가 선택
                    drinkNameInput.setTextColor(ResourcesCompat.getColor(resources, R.color.disable_input_filed, null))
                    drinkNameInput.setHintTextColor(ResourcesCompat.getColor(resources, R.color.disable_input_filed, null))
                } else {
                    // 체크박스가 선택되지 않았을 때
                    drinkNameInput.setTextColor(ResourcesCompat.getColor(resources, R.color.enable_input_filed, null))
                    drinkNameInput.setHintTextColor(ResourcesCompat.getColor(resources, R.color.enable_input_filed, null))
                }
            }

            drinkCountLayout.setOnClickListener {
                drinkCountInput.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(drinkCountInput, InputMethodManager.SHOW_IMPLICIT)
            }

            //drinkCountTitle.setMarginTopBottomInDp(32, 12)

            with(drinkCountInput) {
                setText(drinkCount.toString())
                var isChangedByUser = false
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        isChangedByUser = true
                    }
                    override fun afterTextChanged(p0: Editable?) {
                        if (p0.toString().isBlank() && !isChangedByUser) {
                            setText("0.0") // 기본 값 설정 0.0
                            this@with.setSelection(this@with.text?.length ?: 0)
                            isChangedByUser = false
                        } else {
                            drinkCount = this@with.text.toString().toDoubleOrNull() ?: 0.0
                        }
                    }
                })
            }

            minus.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    // minus 버튼 클릭 시 실행할 코드
                    if(drinkCount > 0){
                        drinkCount -= 0.5
                        if (drinkCount < 0.0) { // drinkCount가 0.0 이하라면
                            drinkCount = 0.0 // drinkCount를 0.0으로 강제 설정
                        }
                        val formattedDrinkCount = String.format("%.1f", drinkCount) // 소수점 이하 한 자리까지만 표시
                        drinkCountInput.setText(formattedDrinkCount)
                        drinkCountInput.setSelection(drinkCountInput.text?.length ?: 0) // 커서를 끝 부분으로 이동
                    }
                }
                true
            }


            plus.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    // plus 버튼 클릭 시 실행할 코드
                    drinkCount += 0.5
                    val formattedDrinkCount = String.format("%.1f", drinkCount) // 소수점 이하 두 자리까지만 표시
                    drinkCountInput.setText(formattedDrinkCount)
                    drinkCountInput.setSelection(drinkCountInput.text?.length ?: 0) // 커서를 끝 부분으로 이동
                }
                true
            }

            // 스피너 데이터 설정
            val spinnerItems = listOf("병", "잔")
            val adapter = ArrayAdapter(this@WritingDiaryActivity, R.layout.dropdown_drink_unit_popup_item, spinnerItems)
            (drinkUnitSpinner as? AutoCompleteTextView)?.setAdapter(adapter)
            // 초기값 설정.
            drinkUnitSpinner?.setText(spinnerItems[0], false)

            drinkUnitSpinner?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                selectedDrinkUnitValue = spinnerItems[position]
            }

            //초기 기본 해쉬태그
            //해쉬태그
           /*val keywords = hashTag.split(",") // 쉼표를 기준으로 문자열을 나눠 리스트로 만듦
            if (keywords != null) {
                selectKeyword.addAll(keywords)
            } // 리스트에 있는 모든 요소를 set에 추가함
            hashtagTitle.setMarginTopBottomInDp(32, 12)
            //태그
            binding.recordSub.chipGroup.removeAllViews()
            val tag = hashTag
            val list = tag.split(",").distinct().toList()
            list.forEach { item ->
                if (item.isNotBlank() && !item.isNullOrBlank() && item != "null") {
                    addChipToGroup(item, emotion)
                }
            }*/

            //초기 기본 해쉬태그
            val keywords = hashTag.split(",").distinct().filter { it.isNotBlank() && it != "null" }
            selectKeyword.addAll(keywords)
            //hashtagTitle.setMarginTopBottomInDp(32, 12)
            //태그 변경
            binding.recordSub.chipGroup.removeAllViews()
            keywords.forEach { item -> addChipToGroup(item, emotion) }

            regDiaryBtn.setOnClickListener {
                lifecycleScope.launch {
                    if (checkboxDrinkName.isChecked || !drinkNameInput.text.isNullOrBlank()) {
                        binding.progressBar.visibility = View.VISIBLE

                        diaryAndroidViewModel.regDiaryDetail(
                            diaryDt = diaryDt,
                            id = id,
                            drinkType = drinkType,
                            emotion = emotion,
                            content = memoInput.text.toString(),
                            tag = selectKeyword.joinToString(","),
                            drinkCount = drinkCountInput.text.toString().toDoubleOrNull() ?: 0.0,
                            drinkUnit = selectedDrinkUnitValue,
                            drinkName = drinkNameInput.text.toString(),
                            imageFiles = imageBody ?: mutableListOf()
                        )
                    } else {
                        Toast.makeText(this@WritingDiaryActivity, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun createHashtags() {
        val input = binding.recordSub.hashtagInput.text.toString()
        if (input.isNotBlank()) {
            selectKeyword.add(input)
            addChipToGroup(input, emotion ?: 0)
            binding.recordSub.hashtagInput.setText("")
        }
    }

    private fun addChipToGroup(tag: String, emotion: Int) {
        val chip = createChip(tag)
        binding.recordSub.chipGroup.addView(chip)

        // 칩이 추가된 후 스크롤 위치를 변경
        binding.recordSub.horizontalScrollView.post {
            binding.recordSub.horizontalScrollView.fullScroll(View.FOCUS_RIGHT)
            binding.recordSub.hashtagInput.clearFocus()
        }
    }

    private fun createChip(tag: String): Chip {
        val chip = LayoutInflater.from(this).inflate(R.layout.chip_item, binding.recordSub.chipGroup, false) as Chip
        chip.minWidth = 0
        chip.text = "#$tag"
        chip.setOnCloseIconClickListener {
            selectKeyword.remove(tag)
            binding.recordSub.chipGroup.removeView(chip)
        }
        chip.setOnLongClickListener {
            editingChip?.let {
                selectKeyword.add(it.text.toString().replace("#", "")) // 수정 취소 시 이전 칩 버튼 다시 추가
                binding.recordSub.chipGroup.addView(it)
            }
            selectKeyword.remove(tag)
            editingChip = chip // 현재 수정 중인 칩 버튼 저장
            val tagWithoutHash = tag.substring(0)
            binding.recordSub.hashtagInput.setText(tagWithoutHash)
            binding.recordSub.hashtagInput.setSelection(binding.recordSub.hashtagInput.text?.length ?: 0) // 커서를 끝 부분으로 이동
            binding.recordSub.hashtagInput.requestFocus()
            binding.recordSub.chipGroup.removeView(chip)

            binding.recordSub.hashtagInput.requestFocus()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.recordSub.hashtagInput, InputMethodManager.SHOW_IMPLICIT)

            true
        }
        return chip
    }
}
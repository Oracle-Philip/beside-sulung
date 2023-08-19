package com.rummy.sulung.view.ui.diary

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.rummy.sulung.R
import com.rummy.sulung.TestActivity
import com.rummy.sulung.common.App
import com.rummy.sulung.common.Prefs
import com.rummy.sulung.databinding.ActivityEditingDiaryBinding
import com.rummy.sulung.network.response.ReadDiaryDetailResponse
import com.rummy.sulung.view.MainActivity
import com.rummy.sulung.view.ui.addRecord.*
import com.rummy.sulung.viewmodel.diary.DiaryAndroidViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import android.os.Environment
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*
import java.io.*
import java.lang.Float.min
import kotlin.math.roundToInt

class ModifyDiaryActivity : DiaryBaseActivity() {
    lateinit var toolbar: Toolbar
    lateinit var binding : ActivityEditingDiaryBinding

    var diaryInfo : ReadDiaryDetailResponse? = null

    companion object{
        const val ID = "id"
        const val DIARY_INFO = "diary_info"
    }

    val diaryAndroidViewModel : DiaryAndroidViewModel by viewModels{
        DiaryAndroidViewModel.Factory(App.instance)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val recyclerViewMargin = 20.dpToPx() * 2 // Assuming RecyclerView has 20dp margin on both sides
        val screenWidth = displayMetrics.widthPixels - recyclerViewMargin
        val spacing = 8.dpToPx() // dp to pixel conversion
        val itemCountPerRow = 3
        val totalSpacing = spacing * (itemCountPerRow - 1) // 각 행의 첫 번째와 마지막 아이템에 대한 간격을 제외한 총 간격
        val itemWidth = (screenWidth - totalSpacing) / itemCountPerRow

        val intent : Intent by lazy {
            getIntent()
        }

        diaryAndroidViewModel.regDiary.observe(this, androidx.lifecycle.Observer<Boolean> {
            if(it){
                binding.progressBar.visibility = View.GONE
                showPopup(binding.root)
            }else{
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "일기 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })

        diaryInfo = intent.getParcelableExtra<ReadDiaryDetailResponse>("diary_info")

        /**
         * 수정을 위한 기존 데이터 값 가져오기
         */

        //해쉬태그
        val keywords = diaryInfo?.tag?.split(",") // 쉼표를 기준으로 문자열을 나눠 리스트로 만듦
        if (keywords != null) {
            selectKeyword.addAll(keywords)
        } // 리스트에 있는 모든 요소를 set에 추가함

        //날짜
        selectdate = diaryInfo?.diaryDt
        val date = selectdate?.let { Date(it) }
        val sdf = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
        val formattedDate = sdf.format(date)


        //술단위
        selectedDrinkUnitValue = diaryInfo?.drink?.unit ?: "병"

        binding = ActivityEditingDiaryBinding.inflate(layoutInflater)
        binding.parentLayout.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS

        toolbar = binding.toolbar.root

        binding.toolbar.toolbarBack.setOnClickListener {
            onBackPressed()
        }

        binding.apply {
            with(toolbar){
                toolbarTitle.text = getString(R.string.editing_diary_text1)
            }
        }

        supportActionBar?.setDisplayShowTitleEnabled(false)
        setContentView(binding.root)

        with(binding.recordSub){
            //날짜 대입
            dateInput.setText(formattedDate.toString())
            //setMarginTopBottomInDp()
            //title1.setMarginTopBottomInDp(40, 12)

            //날짜 선택
            selectDate.setEndIconOnClickListener{
                val today = GregorianCalendar()
                val year: Int = today.get(Calendar.YEAR)
                val month: Int = today.get(Calendar.MONTH)
                val date: Int = today.get(Calendar.DATE)
                val dlg = DatePickerDialog(this@ModifyDiaryActivity, object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                        dateInput.setText("${year}년 ${month+1}월 ${dayOfMonth}일")
                        selectdate = GregorianCalendar(year, month, dayOfMonth).timeInMillis
                    }
                }, year, month, date)
                dlg.show()
            }

            // 기존 값에 따라 술, 감정 아이템 선택
            drinkType = diaryInfo?.drink?.type ?: 0
            emotion = diaryInfo?.emotion ?: 0

            //title2.setMarginTopBottomInDp(32, 12)

            val selectedDrinkTypeItem = alchohol_list.firstOrNull { it.third == drinkType }
            if (selectedDrinkTypeItem != null) {
                val index = alchohol_list.indexOf(selectedDrinkTypeItem)
                alchohol_list[index] = Triple(selectedDrinkTypeItem.first, true, selectedDrinkTypeItem.third)
            }

            //title3.setMarginTopBottomInDp(32, 12)

            val selectedEmotionItem = emotion_list.firstOrNull { it.third == emotion }
            if (selectedEmotionItem != null) {
                val index = emotion_list.indexOf(selectedEmotionItem)
                emotion_list[index] = Triple(selectedEmotionItem.first, true, selectedEmotionItem.third)
            }

            val recyclerView = alcoholList
            recyclerView.layoutManager = GridLayoutManager(this@ModifyDiaryActivity, 3, GridLayoutManager.VERTICAL, false)
            recyclerView.addItemDecoration(RecordActivity_replace.ItemSpacingDecoration(
                8.dpToPx().toFloat(), 3
            ))
            recyclerView.adapter = Editing_Item_Adapter(alchohol_list, itemWidth).apply {
                setOnItemClickListener(object : Editing_Item_Adapter.OnItemClickListener {
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
            emotion_recyclerView.layoutManager = GridLayoutManager(this@ModifyDiaryActivity, 3, GridLayoutManager.VERTICAL, false)
            emotion_recyclerView.addItemDecoration(RecordActivity_replace.ItemSpacingDecoration(
                8.dpToPx().toFloat(), 3
            ))
            emotion_recyclerView.adapter = Editing_Item_Adapter(emotion_list, itemWidth).apply {
                setOnItemClickListener(object : Editing_Item_Adapter.OnItemClickListener {
                    override fun onItemClick(value: Int) {
                        emotion = value // 클릭한 아이템의 포지션을 drinkType 변수에 저장
                    }
                })
            }

            //다이어리제목, 다이어리 이름 인풋필드에서 바깥을 터치시 키보드 숨기기
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

            //상세한 기록 binding
//            memoInput.setText(diaryInfo?.content.toString())
            //title4.setMarginTopBottomInDp(32, 12)

            val content = diaryInfo?.content
            if (content == null || content.equals("null", ignoreCase = true) || content.isEmpty()) {
                memoInput.setText("")
            } else {
                memoInput.setText(content ?: "")
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val imageUrls = diaryInfo?.imageUrls ?: emptyList() // 이미지 URL 리스트

                val deferredImages = imageUrls.map { imageUrl ->
                    async {
                        val bitmap = withContext(Dispatchers.IO) {
                            imageUrl?.let { downloadImage(it) }
                        }

                        if (bitmap != null) {
                            val file = File.createTempFile("image", ".png")
                            val outputStream = FileOutputStream(file)
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
                            MultipartBody.Part.createFormData("imageFiles", file.name, requestFile)
                        } else {
                            null
                        }
                    }
                }

                val imageParts = deferredImages.awaitAll().filterNotNull()
                imageBody.addAll(imageParts)

                withContext(Dispatchers.Main) {
                    val images: MutableList<Any> =
                        mutableListOf<Any>().apply { addAll(imageUrls!!.map { it as Any }) }
                    imageAdapter = ImageAdapter(images)

                    binding.loading.visibility = View.GONE

                    /**
                     * @desc 스크롤뷰 화면 중간으로 가는 부분 떄문에 block했다가 해
                     */
                    binding.parentLayout.post {
                        binding.parentLayout.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
                    }

                    imageAdapter.setOnImageDeleteListener(object :
                        ImageAdapter.OnImageDeleteListener {
                        override fun onImageDeleted(position: Int) {
                            imageAdapter.deleteImage(position)
                            imageBody.removeAt(position)
                        }
                    })

                    imageAdapter.setOnImageAddListener { position ->
                        selectedPosition = position
                        requestPermissions()
                    }

                    val layoutManager = GridLayoutManager(this@ModifyDiaryActivity, 3)
                    imageRecyclerView.layoutManager = layoutManager
                    imageRecyclerView.addItemDecoration(GridSpacingItemDecoration(3, 8, false))
                    imageRecyclerView.adapter = imageAdapter
                    imageRecyclerView.isNestedScrollingEnabled = false // 스크롤 기능 비활성화
                    imageRecyclerView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
                        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                            return rv.scrollState != RecyclerView.SCROLL_STATE_IDLE
                        }
                    })
                }
            }

            //술 이름
            //drinkNameInput.setText(diaryInfo?.drink?.name ?: "")

            //title5.setMarginTopBottomInDp(48, 12)

            val name = diaryInfo?.drink?.name
            if (name == null || name.equals("null", ignoreCase = true) || name.isEmpty()) {
                drinkNameInput.setText("")
                drinkNameInput.setTextColor(ResourcesCompat.getColor(resources, R.color.disable_input_filed, null))
                drinkNameInput.setHintTextColor(ResourcesCompat.getColor(resources, R.color.disable_input_filed, null))
                drinkNameInput.isEnabled = false
                checkboxDrinkName.isChecked = true
            } else {
                drinkNameInput.setText(name ?: "")
                drinkNameInput.setTextColor(ResourcesCompat.getColor(resources, R.color.enable_input_filed, null))
                drinkNameInput.setHintTextColor(ResourcesCompat.getColor(resources, R.color.enable_input_filed, null))
            }

            /**
             * 해쉬태그
             */
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

            //술 이름 인풋박스 활성/비활성화 체크박스
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

            // 음주량 가져오기
            //title6.setMarginTopBottomInDp(32, 12)

            drinkCount = diaryInfo?.drink?.count ?: 1.5

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

            drinkCountLayout.setOnClickListener {
                drinkCountInput.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(drinkCountInput, InputMethodManager.SHOW_IMPLICIT)
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

            // "병", "잔" 스피너 데이터 설정
            val spinnerItems = listOf("병", "잔")
            val adapter = ArrayAdapter(this@ModifyDiaryActivity, R.layout.dropdown_drink_unit_popup_item, spinnerItems)
            (drinkUnitSpinner as? AutoCompleteTextView)?.setAdapter(adapter)
            // 초기값 설정.
            val drinkUnitValue = diaryInfo?.drink?.unit // 기존 데이터에서 음료 단위 값 가져오기
            val selectedIndex = if (drinkUnitValue == "잔") 1 else 0 // "잔"이면 1, "병"이면 0을 selectedIndex에 할당
            //drinkUnitSpinner.setSelection(selectedIndex)

            drinkUnitSpinner?.setText(spinnerItems[selectedIndex], false)

            drinkUnitSpinner?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                selectedDrinkUnitValue = spinnerItems[position]
            }

            //해쉬태그
            //태그
            /**
             * @desc qa0522 태그 수정
             */
            //title7.setMarginTopBottomInDp(32, 12)

            binding.recordSub.chipGroup.removeAllViews()
            val tag = diaryInfo?.tag
            val list = tag?.split(",")?.distinct()?.toList()
            list?.forEach { item ->
                if (item.isNotBlank()) {
                    addChipToGroup(item, diaryInfo?.emotion!!)
                }
            }

            /**
             * @desc 내용이 없어도 수정할 수 있게 하기 QA0508
             */
            modifyDiaryBtn.setOnClickListener {
                lifecycleScope.launch {
                    if (checkboxDrinkName.isChecked || !drinkNameInput.text.isNullOrBlank()) {
                        binding.progressBar.visibility = View.VISIBLE

                        diaryAndroidViewModel.regDiaryDetail(
                            diaryDt = selectdate ?: System.currentTimeMillis(),
                            id = diaryInfo?.id ?: 0,
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
                        Toast.makeText(this@ModifyDiaryActivity, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            }
        }

    private fun createHashtags() {
        val input = binding.recordSub.hashtagInput.text.toString()
        if (input.isNotBlank()) {
            selectKeyword.add(input)
            addChipToGroup(input, diaryInfo?.emotion ?: 0)
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
        /**
         * @desc 칩 사이즈 QA0508
         *
         */
        val chip = LayoutInflater.from(this).inflate(R.layout.chip_item, binding.recordSub.chipGroup, false) as Chip
        chip.minWidth = 0
        chip.text = "#$tag"

        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        chip.measure(widthMeasureSpec, heightMeasureSpec)

        // 추가: 마진 설정
        val layoutParams = chip.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, 0, 0, 0) // 기존 마진 제거
        chip.layoutParams = layoutParams

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val imageFile = data?.getSerializableExtra("imageFile") as? File
            if (imageFile != null) {
                // 여기에서 API 요청을 수행합니다. 파일을 멀티파트 요청으로 보낼 수 있습니다.
                val imageFile = data?.getSerializableExtra("imageFile") as? File
                val requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
                val body = MultipartBody.Part.createFormData("imageFiles", imageFile?.name, requestBody)

                if (selectedPosition < imageAdapter.itemCount - 1) {
                    imageBody[selectedPosition] = body // 기존 이미지를 업데이트
                    imageFile?.absolutePath?.let { imageAdapter.images[selectedPosition] = it }
                } else {
                    imageBody.add(body) // 새 이미지를 추가
                    imageFile?.absolutePath?.let { imageAdapter.addImage(it) }
                }
                imageAdapter.notifyDataSetChanged()
            }
        }
    }
}
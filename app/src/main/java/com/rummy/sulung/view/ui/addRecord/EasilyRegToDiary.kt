package com.rummy.sulung.view.ui.addRecord

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rummy.sulung.R
import com.rummy.sulung.common.App
import com.rummy.sulung.common.DiaryImage
import com.rummy.sulung.common.Prefs
import com.rummy.sulung.databinding.ActivityEasilyRegToDiaryBinding
import com.rummy.sulung.databinding.BottomsheetdialogStoreBinding
import com.rummy.sulung.network.response.ReadDiaryDetailResponse
import com.rummy.sulung.view.MainActivity
import com.rummy.sulung.view.ui.diary.WritingDiaryActivity
import com.rummy.sulung.view.ui.diary.WritingDiaryActivity.Companion.HASH_TAG
import com.rummy.sulung.viewmodel.diary.DiaryAndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EasilyRegToDiary : AppCompatActivity() {

    val TAG = EasilyRegToDiary::class.java.simpleName
    lateinit var toolbar: Toolbar
    lateinit var binding : ActivityEasilyRegToDiaryBinding

    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var bottomSheetBinding : BottomsheetdialogStoreBinding

    companion object{
        const val DIARYDT = "diaryDt"
        const val ID = "id"
        const val DRINKTYPE = "drink_type"
        const val EMOTION = "emotion"
        const val FIRST_RECORD = "first_record"
     }

    var diaryInfo : ReadDiaryDetailResponse? = null

    var diaryDt : Long = -100L
    var id : Int = -100
    var drinkType : Int? = null
    var emotion : Int? = null
    var isFirstRecord = false
    var hashTag : String? = null

    val diaryAndroidViewModel : DiaryAndroidViewModel by lazy{
        ViewModelProvider(this, DiaryAndroidViewModel.Factory(App.instance))
            .get(DiaryAndroidViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent : Intent by lazy {
            getIntent()
        }

        isFirstRecord = intent.getBooleanExtra(FIRST_RECORD, false)

        binding = ActivityEasilyRegToDiaryBinding.inflate(layoutInflater)

        lifecycleScope.launch(Dispatchers.IO) {
            val response = diaryAndroidViewModel.getDiaryDetailInfo(Prefs.diary_id)
            if (response != null) {
                diaryInfo = response
                hashTag = diaryInfo?.tag
                diaryDt = diaryInfo?.diaryDt ?: 0
                emotion = diaryInfo?.emotion
                drinkType = diaryInfo?.drink?.type

                withContext(Dispatchers.Main) {
                    if (isFirstRecord) {
                        bottomSheetBinding = BottomsheetdialogStoreBinding.inflate(layoutInflater)
                        val firstRecord: LottieAnimationView = bottomSheetBinding.firstRecord
                        firstRecord.setAnimation(
                            DiaryImage.setDrinkLottieImg(
                                emotion = emotion!!,
                                drinkType = drinkType!!
                            )
                        )
                        firstRecord.playAnimation()
                        firstRecord.setOnClickListener {
                            if (!firstRecord.isAnimating) {
                                firstRecord.playAnimation()
                            }
                        }
                        bottomSheetDialog = BottomSheetDialog(
                            this@EasilyRegToDiary,
                            R.style.AppBottomSheetDialogTheme
                        )
                        bottomSheetDialog.setCanceledOnTouchOutside(false)
                        bottomSheetBinding.apply {
                            with(this) {
                                exitIcon.setOnClickListener {
                                    bottomSheetDialog.dismiss()
                                }
                            }
                        }
                        bottomSheetDialog.apply {
                            setContentView(bottomSheetBinding.root)
                        }
                        bottomSheetDialog.show()
                    }

                    binding.apply {
                        alcoholStore.setOnClickListener {
                            lifecycleScope.launch {
                                val intent = Intent(this@EasilyRegToDiary, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                        }

                        drinktypeAndEmotion.setAnimation(
                            DiaryImage.setDrinkLottieImg(
                                emotion = emotion!!,
                                drinkType = drinkType!!
                            )
                        )
                        drinktypeAndEmotion.playAnimation()
                        drinktypeAndEmotion.setOnClickListener {
                            if (!drinktypeAndEmotion.isAnimating) {
                                drinktypeAndEmotion.playAnimation()
                            }
                        }

                        writingDiaryBtn.setOnClickListener {
                            val intent =
                                Intent(this@EasilyRegToDiary, WritingDiaryActivity::class.java)
                            intent.putExtra(ID, Prefs.diary_id)
                            intent.putExtra(DIARYDT, diaryDt)
                            intent.putExtra(DRINKTYPE, drinkType)
                            intent.putExtra(EMOTION, emotion)
                            intent.putExtra(FIRST_RECORD, isFirstRecord)
                            intent.putExtra(HASH_TAG, hashTag)
                            startActivity(intent)
                            finish()
                        }

                        with(toolbar) {
                            toolbarTitle.text = getString(R.string.record_toolbar_title)
                            exitIcon.setOnClickListener {
                                if (!isFirstRecord)
                                    finish()
                                else {
                                    val intent =
                                        Intent(this@EasilyRegToDiary, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
            }
        }

        toolbar = binding.toolbar.root

        supportActionBar?.setDisplayShowTitleEnabled(false)
        setContentView(binding.root)
    }

    /**
     * 뒤로가기 버튼을 눌렀을 때
     */
    override fun onBackPressed() {
        if(!isFirstRecord)
            super.onBackPressed()
        else{
            val intent = Intent(this@EasilyRegToDiary, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
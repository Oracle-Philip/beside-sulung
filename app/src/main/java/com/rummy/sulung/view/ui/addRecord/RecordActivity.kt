package com.rummy.sulung.view.ui.addRecord

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.rummy.sulung.R
import com.rummy.sulung.common.App
import com.rummy.sulung.databinding.ActivityRecordBinding
import com.rummy.sulung.viewmodel.diary.DiaryAndroidViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RecordActivity : AppCompatActivity() {

    val TAG = RecordActivity::class.java.simpleName

    lateinit var toolbar: Toolbar
    lateinit var binding : ActivityRecordBinding

    var drinkType = -100
    var emotion = -100
    var selectdate : Long = System.currentTimeMillis()

    val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        result ->
        run {
            //The lambda expression is unused. If you mean a block, you can use 'run { ... }'
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
        binding = ActivityRecordBinding.inflate(layoutInflater)
        toolbar = binding.toolbar.root

        binding.apply {
            with(toolbar){
                toolbarTitle.text = getString(R.string.record_toolbar_title)
                exitIcon.setOnClickListener {
                    finish()
                }
            }
        }

        with(binding.recordSub){

            dateInput.setText(SimpleDateFormat("yyyy년 M월 d일").format(System.currentTimeMillis()).toString())

            selectDate.setEndIconOnClickListener{
                val today = GregorianCalendar()
                val year: Int = today.get(Calendar.YEAR)
                val month: Int = today.get(Calendar.MONTH)
                val date: Int = today.get(Calendar.DATE)
                val dlg = DatePickerDialog(this@RecordActivity, object : DatePickerDialog.OnDateSetListener {
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
                        val intent = Intent(this@RecordActivity, EasilyRegToDiary::class.java)
                        intent.putExtra(EasilyRegToDiary.DRINKTYPE, drinkType)
                        intent.putExtra(EasilyRegToDiary.EMOTION, emotion)
                        intent.putExtra(EasilyRegToDiary.DIARYDT, selectdate)
                        //intent.putExtra(EasilyRegToDiary.ID, id)
                        requestLauncher.launch(intent)
                    }
                    else
                        Toast.makeText(this@RecordActivity, "술과 감정을 선택해 주세요.", Toast.LENGTH_SHORT).show()
                }
            }

            drinkTypeSoju.setOnClickListener {
                drinkType = 0
            }
            drinkTypeBeer.setOnClickListener {
                drinkType = 1
            }
            drinkTypeMakgeolli.setOnClickListener {
                drinkType = 2
            }
            drinkTypeLiquor.setOnClickListener {
                drinkType = 3
            }
            drinkTypeCocktail.setOnClickListener {
                drinkType = 4
            }
            drinkTypeWine.setOnClickListener {
                drinkType = 5
            }
            drinkTypeSake.setOnClickListener {
                drinkType = 6
            }
            drinkTypeChampagne.setOnClickListener {
                drinkType = 7
            }
            drinkTypeHighball.setOnClickListener {
                drinkType = 8
            }

            emotionPleasure.setOnClickListener {
                emotion = 0
            }
            emotionSadness.setOnClickListener {
                emotion = 1
            }
            emotionDepressed.setOnClickListener {
                emotion = 2
            }
            emotionAggro.setOnClickListener {
                emotion = 3
            }
            emotionTranquility.setOnClickListener {
                emotion = 4
            }
            emotionContrats.setOnClickListener {
                emotion = 5
            }
            emotionIntoxication.setOnClickListener {
                emotion = 6
            }
        }

        supportActionBar?.setDisplayShowTitleEnabled(false)
        setContentView(binding.root)
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.record_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.exit_icon -> {
//                finish()
//                return true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
}
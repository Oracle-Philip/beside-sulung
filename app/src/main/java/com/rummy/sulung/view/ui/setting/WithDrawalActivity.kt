package com.rummy.sulung.view.ui.setting

import android.content.Intent
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rummy.sulung.R
import com.rummy.sulung.common.Prefs
import com.rummy.sulung.databinding.ActivityTestBinding
import com.rummy.sulung.databinding.ActivityWithdrawalBinding
import com.rummy.sulung.databinding.BottomsheetdialogStoreBinding
import com.rummy.sulung.network.signin.UserViewModel
import com.rummy.sulung.network.userInfo.UserInfoResponse
import com.rummy.sulung.view.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WithDrawalActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var binding : ActivityWithdrawalBinding

    val userViewModel : UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 상태 표시줄 색상 변경
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.setting_bg)
        }

        val userInfo = intent.getParcelableExtra<UserInfoResponse>("userInfo")

        binding = ActivityWithdrawalBinding.inflate(layoutInflater)
        toolbar = binding.toolbar.root
        binding.apply {
            with(this){
                binding.userName.text = userInfo?.nickName

                toolbar.toolbarTitle.text = getString(R.string.withdrawal)

                withdrawalBtn.setOnClickListener {
                    if(withdrawalCheckBox.isChecked){
                        showPopup()
                    }else{
                        Toast.makeText(this@WithDrawalActivity, "동의 체크해주세요", Toast.LENGTH_SHORT).show()
                    }
                }
                notWithdrawalBtn.setOnClickListener {
                    onBackPressed()
                }
            }
        }

        toolbar.apply {
            setNavigationIcon(R.drawable.btn_arrow)
            setNavigationOnClickListener{
                onBackPressed()
            }
        }

        supportActionBar?.setDisplayShowTitleEnabled(false)
        setContentView(binding.root)
    }

    private fun showPopup() {
        val popupView = LayoutInflater.from(this).inflate(R.layout.with_drawal_popup, null)
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
            findViewById<Button>(R.id.withdrawal).setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO){
                    userViewModel.withDrawal()

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
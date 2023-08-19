package com.rummy.sulung.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.common.util.Utility
import com.rummy.sulung.R
import com.rummy.sulung.common.App.Companion.TAG
import com.rummy.sulung.common.Prefs
import com.rummy.sulung.databinding.ActivitySplashScreenBinding
import com.rummy.sulung.network.userInfo.UserInfoViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {
    lateinit var binding: ActivitySplashScreenBinding

    var diarycount = -100

    val userInfoViewModel : UserInfoViewModel by lazy {
        ViewModelProvider(this).get(UserInfoViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var keyHash = Utility.getKeyHash(this)


        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.main_bg_color)
        }

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            Prefs.Token?.let {
                val userInfoResponseData = async { userInfoViewModel.UserInfo() }.await()?.body()

                if (userInfoResponseData != null && userInfoResponseData.diaryCount > 0) {
                    Log.e(TAG, userInfoResponseData.diaryCount.toString())
                    startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                    finish()
                } else if(userInfoResponseData != null && userInfoResponseData.diaryCount == 0) {
                    if (Prefs.terms_nickname) {
                        Log.e(TAG, userInfoResponseData?.diaryCount.toString())
                        startActivity(Intent(this@SplashScreen, EmptyDataMainActivity::class.java))
                        finish()
                    } else{
                        startActivity(Intent(this@SplashScreen, TermsNicknameActivity::class.java))
                        finish()
                    }
                }
            } ?: run {
                startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
                finish()
            }

//            if(Prefs.Token != null){
//                val userInfoResponseData = async { userInfoViewModel.UserInfo() }.await()?.body()
//
//                if (userInfoResponseData != null && userInfoResponseData.diaryCount > 0) {
//                    Log.e(TAG, userInfoResponseData.diaryCount.toString())
//                    startActivity(Intent(this@SplashScreen, MainActivity::class.java))
//                    finish()
//                } else if(userInfoResponseData != null && userInfoResponseData.diaryCount == 0) {
//                    if (Prefs.terms_nickname) {
//                        Log.e(TAG, userInfoResponseData?.diaryCount.toString())
//                        startActivity(Intent(this@SplashScreen, EmptyDataMainActivity::class.java))
//                        finish()
//                    } else{
//                        startActivity(Intent(this@SplashScreen, TermsNicknameActivity::class.java))
//                        finish()
//                    }
//                }
//            }else {
//                startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
//                finish()
//            }

        }
    }

    override fun onBackPressed() {

    }
}
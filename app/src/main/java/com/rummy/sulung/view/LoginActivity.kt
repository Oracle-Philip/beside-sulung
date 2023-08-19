package com.rummy.sulung.view

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.rummy.sulung.R
import com.rummy.sulung.backpressed.PressedForFinish
import com.rummy.sulung.common.App.Companion.TAG
import com.rummy.sulung.common.Prefs
import com.rummy.sulung.databinding.ActivityLoginBinding
import com.rummy.sulung.network.signin.UserViewModel
import com.rummy.sulung.network.userInfo.UserInfoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    val userViewModel: UserViewModel by lazy {
        ViewModelProvider(this).get(UserViewModel::class.java)
    }

    val userInfoViewModel: UserInfoViewModel by lazy {
        ViewModelProvider(this).get(UserInfoViewModel::class.java)
    }

    var isConsentedFalse: Boolean = false
    var diaryCount: Int = -100

    private lateinit var binding: ActivityLoginBinding
    private lateinit var pressedForFinish: PressedForFinish

    private val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "로그인 실패 $error")
        } else if (token != null) {
            Log.e(TAG, "로그인 성공 ${token.accessToken}")

            Prefs.KAKAO_Token = token.accessToken
            Prefs.KAKAO_REFRESH_Token = token.refreshToken

            signInAndUpdateLiveData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.main_bg_color)
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.btn.setOnClickListener {
            defaultLogin()
        }
        pressedForFinish = PressedForFinish(this)
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        pressedForFinish.onBackPressed()
    }

    private fun filterTerms(userTerm: Boolean): Boolean {
        return userTerm
    }

    private fun defaultLogin() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e(TAG, "로그인 실패 $error")
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    } else {
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback)
                    }
                } else if (token != null) {
                    Prefs.KAKAO_Token = token.accessToken
                    Prefs.KAKAO_REFRESH_Token = token.refreshToken

                    signInAndUpdateLiveData()
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback)
        }
    }

    private fun signInAndUpdateLiveData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val deffered = async {
                userViewModel.signin(Prefs.KAKAO_Token!!, Prefs.KAKAO_REFRESH_Token!!)
            }
            deffered.await()?.let {
                val result = it.userTerms?.map { it?.isConsented }?.filter { it == false  }

                isConsentedFalse = (result?.isNotEmpty() == true)

                if (isConsentedFalse) {
                    Prefs.terms_nickname = false
                    startActivity(Intent(this@LoginActivity, TermsNicknameActivity::class.java))
                } else {
                    Prefs.terms_nickname = true
                    lifecycleScope.launch(Dispatchers.IO) {
                        val userInfoDefferd = async {
                            userInfoViewModel.UserInfo()
                        }
                        val response = userInfoDefferd.await()
                        response?.let{
                            if(it.body()?.diaryCount!! > 0)
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            else
                                startActivity(Intent(this@LoginActivity, EmptyDataMainActivity::class.java))
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}

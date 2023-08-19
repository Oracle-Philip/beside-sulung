package com.rummy.sulung.view.ui.setting

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceFragmentCompat
import com.rummy.sulung.R
import com.rummy.sulung.common.Prefs
import com.rummy.sulung.databinding.FragmentSettingBinding
import com.rummy.sulung.network.userInfo.UserInfoResponse
import com.rummy.sulung.network.userInfo.UserInfoViewModel
import com.rummy.sulung.view.LoginActivity
import com.rummy.sulung.view.MainActivity
import com.rummy.sulung.view.terms.TermsOfPersonalInfoActivity
import com.rummy.sulung.view.terms.TermsOfServiceActivity
import kotlinx.coroutines.launch
import retrofit2.Response

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private var mainActivity : MainActivity? = null

    var userInfo : UserInfoResponse? = null

    val userInfoViewModel: UserInfoViewModel by viewModels()

    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 상태 표시줄 색상 변경

        viewLifecycleOwner.lifecycleScope.launch {
            userInfoViewModel.UserInfo()
        }

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userInfoViewModel.userInfoResponseData.observe(viewLifecycleOwner, {
            if(it != null){
                userInfo = it
                binding.diaryCount.text = it.diaryCount.toString()
                binding.diaryAfter.text = it.lastDiaryDays.toString()
                binding.toolbar.userName.text = it.nickName.toString()
            }
        })

        with(binding) {
/*            binding.diaryCount.text = userData?.diaryCount.toString()
            binding.diaryAfter.text = userData?.lastDiaryDays.toString()*/

            servicePolicy.setOnClickListener {
                startActivity(Intent(mainActivity, TermsOfServiceActivity::class.java))
            }
            personalInfoPolicy.setOnClickListener {
                startActivity(Intent(mainActivity, TermsOfPersonalInfoActivity::class.java))
            }
            inquery.setOnClickListener {
                val pInfo = activity?.packageManager?.getPackageInfo(activity?.packageName ?: "", 0)
                val version = pInfo?.versionName

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("thedrunken13@gmail.com"))
                intent.putExtra(Intent.EXTRA_SUBJECT, "문의하기")
                intent.putExtra(Intent.EXTRA_TEXT, "본문")
                intent.putExtra(Intent.EXTRA_TEXT, "기기의 OS : ${Build.VERSION.RELEASE}, 앱 버전 : ${version}\n사용자 id: ${userInfo?.id}")
                if (intent.resolveActivity(activity?.packageManager!!) != null) {
                    intent.setPackage("com.google.android.gm")
                    startActivity(intent)
                } else {
                    Toast.makeText(activity, "Gmail 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            logout.setOnClickListener {
                showPopup()
            }
            withdrawal.setOnClickListener {
                val intent = Intent(mainActivity, WithDrawalActivity::class.java).apply {
                    putExtra("userInfo", userInfo)
                }
                startActivity(intent)
            }

            return root
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showPopup() {
        val popupView = LayoutInflater.from(context).inflate(R.layout.logout_popup, null)
        val popupWindow = PopupWindow(popupView, 300.dpToPx(), 200.dpToPx())

        // 팝업의 배경 색상과 외곽선 설정
        popupWindow.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_rounded_border3, null))
        popupWindow.elevation = 10F

        // 팝업 외부 영역 터치 불가능 설정
        popupWindow.setOutsideTouchable(false)

        // 팝업의 크기와 위치를 조정
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        // 팝업 뷰 내부의 위젯에 이벤트 핸들러 등록
        with(popupView){
            findViewById<ImageView>(R.id.exit).setOnClickListener {
                popupWindow.dismiss()
            }
            findViewById<Button>(R.id.cancel).setOnClickListener {
                popupWindow.dismiss()
            }
            findViewById<Button>(R.id.logout).setOnClickListener {
                Prefs.Token = null
                Prefs.nickname = null
                startActivity(Intent(mainActivity, LoginActivity::class.java))
                mainActivity?.finish()
                popupWindow.dismiss()
            }
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }
}
package com.rummy.sulung.view.nickname


import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.rummy.sulung.R
import com.rummy.sulung.common.Prefs
import com.rummy.sulung.databinding.FragmentNicknameInputFragmentBinding
import com.rummy.sulung.network.signin.UserViewModel
import com.rummy.sulung.network.userInfo.UserInfoViewModel
import com.rummy.sulung.view.EmptyDataMainActivity
import com.rummy.sulung.view.LoginActivity
import com.rummy.sulung.view.MainActivity
import com.rummy.sulung.view.TermsNicknameActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NicknameOfFragment2 : Fragment() {
    val TAG = NicknameOfFragment2::class.java.simpleName
    lateinit var termsNicknameActivity: TermsNicknameActivity
    lateinit var binding: FragmentNicknameInputFragmentBinding

    private var nickname: String? = null

    val userViewModel : UserViewModel by viewModels()

    val userInfoViewModel : UserInfoViewModel by viewModels()

    companion object {
        fun newInstance() = NicknameOfFragment2()
    }

    private lateinit var viewModel: NicknameOfFragment2ViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        termsNicknameActivity = context as TermsNicknameActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, TAG)
        binding = FragmentNicknameInputFragmentBinding.inflate(inflater, container, false)
        userViewModel.isNickNameSuccess.observe(viewLifecycleOwner, Observer<Boolean>{
            if(it){
                startActivity(Intent(termsNicknameActivity, EmptyDataMainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nickname = arguments?.getString("nickname")

        with(binding){
            parentLayout.setOnTouchListener { v, event ->
                nickNameInput.clearFocus()
                false
            }
            btn.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO){
                    if(nickname != nickNameInput.text.toString())
                        userViewModel.modNickName(nickNameInput.text.toString())
                    else{
                        withContext(Dispatchers.Main){
                            Prefs.terms_nickname = true
                            startActivity(Intent(termsNicknameActivity, EmptyDataMainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            })
                        }
                    }
                }
            }
            nickNameInput.setText(nickname)
            if(validateNickname()){
                with(btn){
                    isEnabled = true
                }
            }else{
                with(btn) {
                    isEnabled = false
                }
            }
            nickNameInput.addTextChangedListener (object : TextWatcher{
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun afterTextChanged(getText: Editable?) {
                        if(validateNickname()){
                            with(btn){
                                isEnabled = true
                            }
                        }else{
                            with(btn) {
                                isEnabled = false
                            }
                        }
                    }
                })
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NicknameOfFragment2ViewModel::class.java)
    }

    private fun validateNickname(): Boolean {
        val value: String = binding.nickNameInput?.text.toString()
        val nicknamePattern = "^[ㄱ-ㅣ가-힣a-zA-Z0-9]{2,7}+$"

        return if (value.isEmpty()) {
            binding.nickNameInputLayout.error = getString(R.string.nickname_input_error_empty)
            false
        } else if (!value.matches(nicknamePattern.toRegex())) {
            binding.nickNameInputLayout.error = getString(R.string.nickname_input_error)
            false
        } else {
            binding.nickNameInputLayout.error = null
            binding.nickNameInputLayout.isErrorEnabled = false
            true
        }
    }

    /*private fun showPopup() {
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
                popupWindow.dismiss()
            }
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }*/
}
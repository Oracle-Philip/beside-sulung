package com.rummy.sulung.view.terms

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer

import androidx.lifecycle.lifecycleScope
import com.rummy.sulung.R
import com.rummy.sulung.databinding.FragmentTermsOfFragment1Binding
import com.rummy.sulung.network.signin.UserViewModel
import com.rummy.sulung.network.userInfo.UserInfoViewModel
import com.rummy.sulung.view.TermsNicknameActivity
import com.rummy.sulung.viewmodel.TermsViewModel
import kotlinx.coroutines.launch

class TermsOfFragment1 : Fragment() {

    companion object {
        fun newInstance() = TermsOfFragment1()
    }

    val userViewModel : UserViewModel by lazy{
        ViewModelProvider(this).get(UserViewModel::class.java)
    }

    val userInfoViewModel : UserInfoViewModel by lazy {
        ViewModelProvider(this).get(UserInfoViewModel::class.java)
    }

    val termsViewModel : TermsViewModel by lazy {
        ViewModelProvider(termsNicknameActivity).get(TermsViewModel::class.java)
    }

    lateinit var _binding: FragmentTermsOfFragment1Binding
    val binding : FragmentTermsOfFragment1Binding
    get() = _binding

    lateinit var viewModel: TermsOfFragment1ViewModel
    lateinit var termsNicknameActivity: TermsNicknameActivity

    val TAG = TermsOfFragment1::class.java.simpleName
    val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){

        val resultData = it.data?.getBooleanExtra("result", false)
        Log.e(TAG, resultData.toString())
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        termsNicknameActivity = context as TermsNicknameActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_terms_of_fragment1, null, false)

        userViewModel.isConsentedSuccess.observe(viewLifecycleOwner, Observer<Boolean> {
            if(it){
                termsNicknameActivity.moveToNickname()
            }
        })

        with(binding){
            termsviewmodel = termsViewModel
            lifecycleOwner = this@TermsOfFragment1
            btn.setOnClickListener {
                lifecycleScope.launch {
                    userViewModel.regTerms(isConsented = checkBoxAll.isChecked, termsType = 0)
                }
            }
            terms1.setOnClickListener {
                requestLauncher.launch(Intent(termsNicknameActivity, TermsOfServiceActivity::class.java))
            }
            terms2.setOnClickListener {
                requestLauncher.launch(Intent(termsNicknameActivity, TermsOfPersonalInfoActivity::class.java))
            }
            return root
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TermsOfFragment1ViewModel::class.java)
    }
}
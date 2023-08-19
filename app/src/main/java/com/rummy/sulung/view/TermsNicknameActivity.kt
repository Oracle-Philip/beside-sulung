package com.rummy.sulung.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.rummy.sulung.R
import com.rummy.sulung.backpressed.PressedForFinish
import com.rummy.sulung.common.App.Companion.TAG
import com.rummy.sulung.databinding.ActivityTermsNicknameBinding
import com.rummy.sulung.network.userInfo.UserInfoViewModel
import com.rummy.sulung.view.terms.TermsOfFragment1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TermsNicknameActivity : AppCompatActivity() {

    lateinit var binding: ActivityTermsNicknameBinding
    lateinit var navController: NavController
    lateinit var pressedForFinish: PressedForFinish

    val userInfoViewModel : UserInfoViewModel by viewModels()
    var nickname : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.terms_nickname_controller)

        lifecycleScope.launch(Dispatchers.IO) {
            userInfoViewModel.UserInfo()
        }

        userInfoViewModel.userInfoResponseData.observe(this, Observer {
            nickname = it?.nickName
        })

        pressedForFinish = PressedForFinish(this)
    }

    fun moveToNickname(){
        val bundle = bundleOf("nickname" to (nickname ?: ""))
        navController.navigate(R.id.action_navigation_terms_to_navigation_nickname, bundle)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if(navController.currentDestination?.id == navController.graph.startDestinationId){
            pressedForFinish.onBackPressed()
        } else{
            navController.navigate(R.id.action_navigation_nickname_to_navigation_terms)
        }
    }

    private fun getForegroundFragment(): Fragment? {
        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.terms_nickname_controller)
        return navHostFragment?.childFragmentManager?.fragments?.get(0)
    }
}
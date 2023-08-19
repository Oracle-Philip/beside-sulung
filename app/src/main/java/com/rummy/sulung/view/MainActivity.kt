package com.rummy.sulung.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.rummy.sulung.R
import com.rummy.sulung.backpressed.PressedForFinish
import com.rummy.sulung.databinding.ActivityMainBinding
import com.rummy.sulung.databinding.BottomsheetdialogFilterBinding
import com.rummy.sulung.databinding.BottomsheetdialogStoreBinding
import com.rummy.sulung.view.ui.addRecord.RecordActivity
import com.rummy.sulung.view.ui.addRecord.RecordActivity_replace
import com.rummy.sulung.view.ui.diary.DiaryFragment
import com.rummy.sulung.view.ui.store.StoreFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var pressedForFinish: PressedForFinish

    var menu: Menu? = null

    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var bottomSheetBinding: BottomsheetdialogFilterBinding

    lateinit var storeFragment : StoreFragment

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.setting_bg)
        }

        pressedForFinish = PressedForFinish(this)
        bottomSheetBinding = BottomsheetdialogFilterBinding.inflate(layoutInflater)
        bottomSheetDialog =
            BottomSheetDialog(this@MainActivity, R.style.AppBottomSheetDialogTheme)
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

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.recordAddMain.setOnClickListener {
            startActivity(Intent(this@MainActivity, RecordActivity_replace::class.java))
        }

        supportActionBar?.setBackgroundDrawable(ColorDrawable(R.color.light_gray))
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        navView.itemIconTintList = null

        val navController = findNavController(R.id.nav_host_fragment_activity_replace_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_diary, R.id.navigation_store, R.id.navigation_setting
            )
        )
        navController.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            when (destination.id) {
                R.id.navigation_diary -> {
                    supportActionBar?.hide()
                    menu?.getItem(0)?.isVisible = true
                    binding.recordAddMain.visibility = View.VISIBLE
                    setToolbarColor(R.color.transparent)

                    storeFragment?.filteterReset()
                }
                R.id.navigation_store -> {
                    supportActionBar?.hide()
                    menu?.getItem(0)?.isVisible = true
                    binding.recordAddMain.visibility = View.VISIBLE
                    setToolbarColor(R.color.transparent)
                }
                R.id.navigation_setting -> {
                    supportActionBar?.hide()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.statusBarColor = ContextCompat.getColor(this, R.color.setting_bg)
                    }
                    menu?.getItem(0)?.isVisible = false
                    binding.recordAddMain.visibility = View.GONE
                    setToolbarColor(R.color.setting_bg)

                    storeFragment?.filteterReset()
                }
            }
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        pressedForFinish.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter
            -> {
                bottomSheetDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setStoreFragmentInstance(_instance : StoreFragment){
        storeFragment = _instance
    }

    private fun setToolbarColor(colorId: Int) {
        supportActionBar?.apply {
            setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(
                        this@MainActivity,
                        colorId
                    )
                )
            )
            elevation = 0f
        }
    }
}

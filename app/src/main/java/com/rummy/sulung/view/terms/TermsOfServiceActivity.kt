package com.rummy.sulung.view.terms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.rummy.sulung.R
import com.rummy.sulung.databinding.ActivityTermsOfPersonalInfoBinding
import com.rummy.sulung.databinding.ActivityTermsOfServiceBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class TermsOfServiceActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var binding : ActivityTermsOfServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsOfServiceBinding.inflate(layoutInflater)
        with(binding){
            toolbar.toolbarTitle.text = getString(R.string.check_terms_of_service)
            this@TermsOfServiceActivity.toolbar = toolbar.root
            this@TermsOfServiceActivity.toolbar.setNavigationIcon(R.drawable.btn_arrow)
            this@TermsOfServiceActivity.toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
            content22.content.text = getAssetsTestString("terms_of_service.txt")
        }
        setContentView(binding.root)
    }

    fun getAssetsTestString(fileName: String): String{
        val termsString = StringBuilder()
        val reader: BufferedReader

        try {
            reader = BufferedReader(
                InputStreamReader(assets.open(fileName))
            )

            var str: String?
            while (reader.readLine().also { str = it } != null) {
                termsString.append(str)
                termsString.append('\n') //줄 변경
            }
            reader.close()
            return termsString.toString()

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "fail"
    }
}
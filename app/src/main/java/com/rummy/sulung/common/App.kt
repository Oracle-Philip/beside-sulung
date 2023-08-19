package com.rummy.sulung.common

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.rummy.sulung.R
import com.rummy.sulung.database.SulungDatabase
import com.rummy.sulung.network.repository.SulungRepository

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        TAG = "Test"
        // Kakao SDK 초기화
        KakaoSdk.init(this, getString(R.string.native_app_key))
        repository = SulungRepository(SulungDatabase.getDatabase(this))
    }

    companion object {
        lateinit var repository : SulungRepository
        lateinit var instance: App
        lateinit var TAG: String
    }
}
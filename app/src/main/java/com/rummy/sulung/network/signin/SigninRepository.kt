package com.rummy.sulung.network.signin

import android.app.Application
import com.rummy.sulung.common.App

class SigninRepository(application: Application) {
    companion object{
        private var sInstance: SigninRepository? = null
        fun getInstance(): SigninRepository {
            return sInstance
                ?: synchronized(this){
                    val instance = SigninRepository(App.instance)
                    sInstance = instance
                    instance
                }
        }
    }
}
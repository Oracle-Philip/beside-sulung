package com.rummy.sulung.common

import android.preference.PreferenceManager

object Prefs {
    private const val KAKAO_TOKEN = "kakao_token"
    private const val KAKAO_REFRESHTOKEN = "kakao_refreshtoken"
    private const val TOKEN = "token"
    private const val EMAIL = "email"
    private const val NICKNAME = "nickname"
    private const val USER_ID = "user_id"
    private const val TERMS_AGREE = "terms_agree"
    private const val STATUSCODE_TERMS0 = "statusCode_terms0"
    private const val STATISCODEVALUE_TERMS0 = "statusCodeValue_terms0"

    private const val STATUSCODE_NICKNAME0 = "statusCode_nickname0"
    private const val STATISCODEVALUE_NICKNAME0 = "statusCodeValue_nickname0"

    private const val DIARY_ID = "diary_id"

    private const val TERMS_NICKNAME = "terms_nickname"

    val prefs by lazy {
        PreferenceManager
            .getDefaultSharedPreferences(App.instance)
    }

    var terms_nickname
        get() = prefs.getBoolean(TERMS_NICKNAME, false)
        set(value) = prefs.edit()
            .putBoolean(TERMS_NICKNAME, value)
            .apply()

    var diary_id
        get() = prefs.getInt(DIARY_ID, -100)
        set(value) = prefs.edit()
            .putInt(DIARY_ID, value)
            .apply()


    var statusCode_Nickname0
        get() = prefs.getString(STATUSCODE_NICKNAME0, null)
        set(value) = prefs.edit()
            .putString(STATUSCODE_NICKNAME0, value)
            .apply()

    var statusCodeValue_Nickname0
        get() = prefs.getInt(STATISCODEVALUE_NICKNAME0, -1)
        set(value) = prefs.edit()
            .putInt(STATISCODEVALUE_NICKNAME0, value)
            .apply()

    var statusCode_Terms0
        get() = prefs.getString(STATUSCODE_TERMS0, null)
        set(value) = prefs.edit()
            .putString(STATUSCODE_TERMS0, value)
            .apply()

    var statusCodeValue_Terms0
        get() = prefs.getInt(STATISCODEVALUE_TERMS0, -1)
        set(value) = prefs.edit()
            .putInt(STATISCODEVALUE_TERMS0, value)
            .apply()

    var TERMS_Agree
        get() = prefs.getBoolean(TERMS_AGREE, false)
        set(value) = prefs.edit()
            .putBoolean(TERMS_AGREE, value)
            .apply()

    var KAKAO_Token
        get() = prefs.getString(KAKAO_TOKEN, null)
        set(value) = prefs.edit()
            .putString(KAKAO_TOKEN, value)
            .apply()

    var KAKAO_REFRESH_Token
        get() = prefs.getString(KAKAO_REFRESHTOKEN, null)
        set(value) = prefs.edit()
            .putString(KAKAO_REFRESHTOKEN, value)
            .apply()
    var Token
        get() = prefs.getString(TOKEN, null)
        set(value) = prefs.edit()
            .putString(TOKEN, value)
            .apply()

    var Email
        get() = prefs.getString(EMAIL, null)
        set(value) = prefs.edit()
            .putString(EMAIL, value)
            .apply()

    var nickname
        get() = prefs.getString(NICKNAME, null)
        set(value) = prefs.edit()
            .putString(NICKNAME, value)
            .apply()

    var userId
        get() = prefs.getLong(USER_ID, 0)
        set(value) = prefs.edit()
            .putLong(USER_ID, value)
            .apply()
}
package com.rummy.sulung.network.signin

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rummy.sulung.common.App
import com.rummy.sulung.common.Prefs
import com.rummy.sulung.network.request.TermsConcentedRequest
import com.rummy.sulung.network.request.modNickNameRequest
import com.rummy.sulung.network.response.DeleteUserResponse
import com.rummy.sulung.network.response.TermsConcentedResponse
import com.rummy.sulung.network.response.ModNickNameResponse
import com.rummy.sulung.network.response.SigninResponse
import com.rummy.sulung.network.service.SulungApiService
import com.rummy.sulung.view.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class UserViewModel : ViewModel() {
    val TAG = UserViewModel::class.java.simpleName

    val signinSuccess = MutableLiveData<Boolean>()

    val isConsentedSuccess = MutableLiveData<Boolean>()

    val isNickNameSuccess = MutableLiveData<Boolean>()

    val userData = MutableLiveData<SigninResponse>()

    private suspend fun RequestModNickName(request: modNickNameRequest) =
        withContext(Dispatchers.IO){
            SulungApiService.instance.editUser(request)
        }

    private suspend fun RequestRegTerms(request: TermsConcentedRequest) =
        withContext(Dispatchers.IO){
            SulungApiService.instance.regTerms(request)
        }

    private suspend fun RequestSignin(request: SigninRequest) =
        withContext(Dispatchers.IO){
            SulungApiService.instance.signin(request)
        }

    /**
     * 회원탈퇴
     */
    private suspend fun RequestWithDrawal() =
        withContext(Dispatchers.IO){
            SulungApiService.instance.deleteUser()
        }

    suspend fun withDrawal(){
        try{
            val response = RequestWithDrawal()
            withDrawalResponse(response)
        } catch (e: Exception){
        }
    }

    private fun withDrawalResponse(response: Response<DeleteUserResponse>){
        if(response.isSuccessful){
            val result = response.body()
            if(result?.status == "success"){
                Prefs.Token = null
                Prefs.KAKAO_Token = null
                Prefs.KAKAO_REFRESH_Token = null
                Prefs.terms_nickname = false
                App.instance.run {
                    val intent = Intent(App.instance, LoginActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                }
            }
        } else {
            Toast.makeText(App.instance, "회원탈퇴에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun signin(accessToken : String, refreshToken : String) : SigninResponse?{
        val request = SigninRequest(accessToken, refreshToken)

        return try{
            val response = RequestSignin(request)
            onSigninResponse(response)
        } catch (e: Exception){
            null
        }
    }

    suspend fun regTerms(isConsented : Boolean, termsType : Int){
        val request = TermsConcentedRequest(isConsented, termsType)

        try{
            val response = RequestRegTerms(request)
            onTermsConcentedResponse(response)
        } catch (e : Exception){

        }
    }

    suspend fun modNickName(nickName : String){
        val request = modNickNameRequest(nickName)

        try{
            val response = RequestModNickName(request)
            onModNickNameResponse(response)
        } catch (e : Exception){

        }
    }

    private fun onModNickNameResponse(response: Response<ModNickNameResponse>){
        if(response.isSuccessful){
            val result : ModNickNameResponse?= response.body()
//            Prefs.statusCode_Nickname0 = result?.statusCode
//            Prefs.statusCodeValue_Nickname0 = result?.statusCodeValue!!

            Prefs.terms_nickname = true
            isNickNameSuccess.postValue(true)
        }else{
            Toast.makeText(App.instance, "닉네임을 변경에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onTermsConcentedResponse(response: Response<TermsConcentedResponse>){
        if(response.isSuccessful){
            val result : TermsConcentedResponse?= response.body()
            /*Prefs.statusCode_Terms0 = result?.statusCode
            Prefs.statusCodeValue_Terms0 = result?.statusCodeValue!!*/
            isConsentedSuccess.postValue(true)
        }else{
            Toast.makeText(App.instance, "약관에 동의해 주세요", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onSigninResponse(response: Response<SigninResponse>) : SigninResponse?{
        return if(response.isSuccessful){
            val result : SigninResponse? = response.body()
            Prefs.Token = result?.token
            Prefs.nickname = result?.nickname
            Log.e(TAG, result.toString())
            userData.postValue(result!!)
            result
            //signinSuccess.postValue(true)
        }else{
            null
        }
    }
}
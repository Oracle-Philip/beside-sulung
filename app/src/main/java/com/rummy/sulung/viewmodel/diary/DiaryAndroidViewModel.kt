package com.rummy.sulung.viewmodel.diary

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.rummy.sulung.common.App
import com.rummy.sulung.common.App.Companion.TAG
import com.rummy.sulung.common.Prefs
import com.rummy.sulung.database.SulungDatabase.Companion.getDatabase
import com.rummy.sulung.network.repository.SulungRepository
import com.rummy.sulung.network.request.*
import com.rummy.sulung.network.response.DeleteDiaryResponse
import com.rummy.sulung.network.response.ReadDiaryDetailResponse
import com.rummy.sulung.network.response.ReadDiaryListResponse
import com.rummy.sulung.network.response.ReadStoreListResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import retrofit2.Response
import retrofit2.http.Multipart

class DiaryAndroidViewModel(application: Application) : ViewModel() {

    val sulungRepository = App.repository

    lateinit var diaryListInfo : LiveData<ReadDiaryListResponse>
    lateinit var diaryDetailInfo : LiveData<ReadDiaryDetailResponse>
    lateinit var storeListInfo : LiveData<ReadStoreListResponse>

    private val _regDiary = MutableLiveData<Boolean>()
    val regDiary : LiveData<Boolean> = _regDiary

    private val _deleteDiary = MutableLiveData<Boolean>()
    val deleteDiary : LiveData<Boolean> = _deleteDiary

    init {
        viewModelScope.launch {
            diaryListInfo = sulungRepository.diaryListInfo
            diaryDetailInfo = sulungRepository.diaryDetailInfo
            storeListInfo = sulungRepository.storeListInfo
        }
    }
    suspend fun regDiaryEasily(diaryDt : Long, drinkType: Int, emotion: Int){
        val request = DiaryEasyRegRequest(
            diaryDt = diaryDt,
            drinkType = drinkType,
            emotion = emotion
        )
        try{
            val response = sulungRepository.regDiaryEasily(request)
            onRegDiaryEasily(response)
        }catch (e: Exception){}
    }

    suspend fun regDiaryDetail(
        content : String,
        diaryDt : Long,
        drinkCount : Double,
        drinkUnit : String,
        drinkName : String,
        drinkType: Int,
        emotion: Int,
        id : Int,
        imageFiles : List<MultipartBody.Part?>,
        tag : String){
        val request = DiaryDetailRegRequest(
            content = content,
            diaryDt = diaryDt,
            drinkCount = drinkCount,
            drinkUnit = drinkUnit,
            drinkName = drinkName,
            drinkType = drinkType,
            emotion = emotion,
            id = id,
            imageFiles = imageFiles,
            tag = tag
        )
        try{
            val response = sulungRepository.regDiaryDetail(request)
            onRegDiaryDetail(response)
        }catch (e: Exception){
            _regDiary.postValue(false)
        }
    }
    suspend fun deleteDiary(id : Int){
        val request = DeleteDiaryRequest(id)
        try{
            val response = sulungRepository.deleteDiary(request)
            onDeleteDiary(response)
        }catch (e: Exception){}
    }
    suspend fun getDiaryDetailInfo(id : Int) : ReadDiaryDetailResponse?{
        val request = DiaryDetailRequest(
            id = id
        )
        try{
            val response = sulungRepository.getDiaryDetailInfo(request)
            onDiaryDetailInfo(response)
            return response.body()!!
        }catch (e: Exception){
            return null
        }
    }
    suspend fun getDiaryListFirstPageInfo(){
        val request = DiaryListFirstPageRequest(
            size = null,
            sort = "OLDEST"
        )
        try{
            val response = sulungRepository.getDiaryListFirstPageInfo(request)
            onDiaryListInfo(response)
        }catch (e: Exception){

        }
    }
    suspend fun getDiaryListInfo(preCursor : Int?, nextCursor : Int?, size : Int? = 10, sort : String? = "NEWEST"){
        val request = DiaryListRequest(
                preCursor = preCursor,
                nextCursor = nextCursor,
                size = size,
                sort = sort
            )
        try{
            val response = sulungRepository.getDiaryListInfo(request)
            onDiaryListInfo(response)
        }catch (e: Exception){

        }
    }
    suspend fun getStoreListInfo(drinkType: String?, emotion: String?, preCursor: Int?, nextCursor: Int?, size: Int){
        val request = StoreListRequest(
            drinkType = drinkType,
            emotion = emotion,
            preCursor = preCursor,
            nextCursor = nextCursor,
            size = size
        )
        try{
            val response = sulungRepository.getStoreListInfo(request)
            onStoreListInfo(response)
        }catch (e: Exception){

        }
    }

    suspend fun getStoreArrayListInfo(drinkType: Array<Int>, emotion: Array<Int>, preCursor: Int?, nextCursor: Int?, size: Int){
        val request = StoreListArrayRequest(
            drinkType = drinkType,
            emotion = emotion,
            preCursor = preCursor,
            nextCursor = nextCursor,
            size = size
        )
        try{
            val response = sulungRepository.getStoreListInfo2(request)
            onStoreListInfo(response)
        }catch (e: Exception){

        }
    }
    private fun onRegDiaryEasily(response: Response<RegDiaryEasilyResponse>) {
        if(response.isSuccessful){
            val result : RegDiaryEasilyResponse? = response.body()
            //Log.e(TAG, result.toString())
            //Toast.makeText(App.instance, "등록 완료 ${result?.id}", Toast.LENGTH_SHORT).show()
            Prefs.diary_id = result?.id!!
        }else{
            Toast.makeText(App.instance, "등록 실패", Toast.LENGTH_SHORT).show()
        }
    }
    private fun onRegDiaryDetail(response: Response<RegDiaryDetailResponse>) {
        if(response.isSuccessful){
            val result : RegDiaryDetailResponse? = response.body()
            Log.e(TAG, "image ${result.toString()}")
            Prefs.diary_id = result?.id!!
            _regDiary.postValue(true)
            //Toast.makeText(App.instance, "일기 등록 완료 ${result?.id}", Toast.LENGTH_SHORT).show()
        }else{
            //Log.e(TAG, "image 등록 실패")
            _regDiary.postValue(false)
            Toast.makeText(App.instance, "일기 등록 실패", Toast.LENGTH_SHORT).show()
        }
    }
    private fun onDeleteDiary(response: Response<DeleteDiaryResponse>) {
        if(response.isSuccessful){
            val result : DeleteDiaryResponse? = response.body()
            Log.e(TAG, "delete result : ${result.toString()}")
            Prefs.diary_id = -100
            _deleteDiary.postValue(true)
        }else{

        }
    }
    private fun onDiaryDetailInfo(response: Response<ReadDiaryDetailResponse>) {
        if(response.isSuccessful){
            val result : ReadDiaryDetailResponse? = response.body()
            Log.e(TAG, result.toString())
        }else{

        }
    }
    private fun onDiaryListInfo(response: Response<ReadDiaryListResponse>) {
        if(response.isSuccessful){
            val result : ReadDiaryListResponse? = response.body()
            Log.e(TAG, result.toString())
        }else{

        }
    }
    private fun onStoreListInfo(response: Response<ReadStoreListResponse>) {
        if(response.isSuccessful){
            val result : ReadStoreListResponse? = response.body()
            Log.e(TAG, result.toString())
        }else{

        }
    }

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DiaryAndroidViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DiaryAndroidViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
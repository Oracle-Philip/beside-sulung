/*
package com.rummy.sulung.view.ui.diary.paging

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import androidx.paging.*
import androidx.savedstate.SavedStateRegistryOwner
import com.rummy.sulung.common.App
import com.rummy.sulung.database.SulungDatabase
import com.rummy.sulung.network.repository.SulungRepository
import com.rummy.sulung.network.response.Diary
import com.rummy.sulung.network.response.ReadDiaryListResponse
import com.rummy.sulung.network.service.SulungApiService
import com.rummy.sulung.viewmodel.diary.DiaryAndroidViewModel
import kotlinx.coroutines.*
import retrofit2.Response

class PagingAndroidViewModel_(
    application: Application
) : AndroidViewModel(application){

    companion object {
        private const val FIRST_PAGE = 1
        private const val PAGE_SIZE = 10
    }

    private val repository = SulungRepository(SulungDatabase.getDatabase(application)!!)

    val diarys: LiveData<PagedList<Diary>>

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(PAGE_SIZE)
            .build()

//        val factory = DiaryDetailSourceFactory11(
//            { callback -> loadInitial(callback) },
//            { params, callback -> loadAfter(params, callback) })
        val factory = DiaryDetailSourceFactory()

        diarys = LivePagedListBuilder(factory, config)
            .build()
    }

    //        val response = getDiary(Int.MAX_VALUE, NEXT)
//        if(response.isSuccessful){
//            response.body()?.items?.get(0)?.diaries.let{
//                if(it?.isNotEmpty() == true)
//                    callback.onResult(it, it.first().id, it.last().id)
//            }
//        }else{
//            GlobalScope.launch(Dispatchers.Main) {
//                showErrorMessage(response)
//            }
//        }

//        val response = getDiary(Int.MAX_VALUE, NEXT)
//        if(response.isSuccessful){
//            response.body()?.items?.get(0)?.diaries.let{
//                if(it?.isNotEmpty() == true)
//                    callback.onResult(it, it.last().id)
//            }
//        }else{
//            GlobalScope.launch(Dispatchers.Main) {
//                showErrorMessage(response)
//            }
//        }

  */
/*  private fun loadInitial(callback: PageKeyedDataSource.LoadInitialCallback<Int, Diary>) {
        viewModelScope.launch {
            val response = SulungApiService.instance.diaryListFirstPage(preCursor = prev, nextCursor = next, size = 10, sort = "NEWEST")
            if(response.isSuccessful){
                response.body()?.let { diaryList ->
                    if(diaryList.isNotEmpty())
                        callback.onResult(diaryList, FIRST_PAGE, diaryList.last().id)
                }
            }
        }
    }

    private fun loadAfter(
        params: PageKeyedDataSource.LoadParams<Int>,
        callback: PageKeyedDataSource.LoadCallback<Int, Diary>
    ) {
        viewModelScope.launch {
            val response = getDiary(params.key)
            if(response.isSuccessful){
                response.body()?.let { diaryList ->
                    if(diaryList.isNotEmpty())
                        callback.onResult(diaryList, params.key + 1)
                }
            }
        }
    }*//*



    */
/*   private fun loadInitial(callback: PageKeyedDataSource.LoadInitialCallback<Int, Diary>) {
           viewModelScope.launch {
           val response = getDiary(Int.MAX_VALUE, NEXT)
           if(response.isSuccessful){
               response.body()?.items?.get(0)?.diaries.let{
                   if(it?.isNotEmpty() == true)
                       callback.onResult(it, it.first().id, it.last().id)
               }
           }
       }

       private fun loadAfter(
           params: PageKeyedDataSource.LoadParams<Int>,
           callback: PageKeyedDataSource.LoadCallback<Int, Diary>
       ) {
           viewModelScope.launch {
               withContext(Dispatchers.IO){
                   val request = ReadBloodPressureRequest(startDt = srchDate, endDt = srchDate, patientSeqFromC.value!!, pageNo = params.key, countPerPage = PAGE_SIZE)
                   val response = repository.getStoreListInfo(request)

                   val list = response.body()?.resultList

                   callback.onResult(list as List<Diary>, params.key + 1)
               }
           }
   //        val response = getDiary(Int.MAX_VALUE, NEXT)
   //        if(response.isSuccessful){
   //            response.body()?.items?.get(0)?.diaries.let{
   //                if(it?.isNotEmpty() == true)
   //                    callback.onResult(it, it.last().id)
   //            }
   //        }else{
   //            GlobalScope.launch(Dispatchers.Main) {
   //                showErrorMessage(response)
   //            }
               }*//*


    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PagingAndroidViewModel_::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PagingAndroidViewModel_(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

//    class Factory(
//        val app: Application
//    ) : AbstractSavedStateViewModelFactory(owner, null) {
//
//        override fun <T : ViewModel?> create(
//            key: String,
//            modelClass: Class<T>,
//            handle: SavedStateHandle
//        ): T {
//            if (modelClass.isAssignableFrom(PagingAndroidViewModel::class.java)) {
//                @Suppress("UNCHECKED_CAST")
//                return PagingAndroidViewModel(app, handle) as T
//            }
//            throw IllegalArgumentException("Unknown ViewModel class")
//        }
//    }
}*/

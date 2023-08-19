package com.rummy.sulung.network.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.rummy.sulung.common.Event
import com.rummy.sulung.database.SulungDatabase
import com.rummy.sulung.network.request.*
import com.rummy.sulung.network.response.ReadDiaryDetailResponse
import com.rummy.sulung.network.response.ReadDiaryListResponse
import com.rummy.sulung.network.response.ReadDiaryYearMonthResponse
import com.rummy.sulung.network.response.ReadStoreListResponse
import com.rummy.sulung.network.service.SulungApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SulungRepository constructor(private val sulungDatabase: SulungDatabase){

    //2023-01-22
    //eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0IiwiaWF0IjoxNjc0NDM4MzE3LCJleHAiOjQ4MjgwMzgzMTd9.4mvdYEzZ0Iv2wpcDVecOHONAPpoS5EGcrtGCMFq1Epo

//    fun sulungDiartyPagingSource() = DiaryDetailPagingSource(onLoadInitial, onLoadAfter)

    val diaryYearMonthResponse : LiveData<ReadDiaryYearMonthResponse> = Transformations.map(sulungDatabase.sulungDao.getDiaryListYearMonthInfo()){
        it
    }

    val diaryListInfo : LiveData<ReadDiaryListResponse> = Transformations.map(sulungDatabase.sulungDao.getDiaryListInfo()){
        it
    }

    val diaryDetailInfo : LiveData<ReadDiaryDetailResponse> = Transformations.map(sulungDatabase.sulungDao.getDiaryDetailInfo()){
        it
    }
    val storeListInfo : LiveData<ReadStoreListResponse> = Transformations.map(sulungDatabase.sulungDao.getStoreListInfo()){
        it
    }

/*    val _regDiary = MutableLiveData<Boolean>()
    val regDiary : LiveData<Boolean> = _regDiary
    val _deleteDiary = MutableLiveData<Boolean>()
    val deleteDiary : LiveData<Boolean> = _deleteDiary*/
    val _regDiary = MutableLiveData<Event<Boolean>>()
    val regDiary: LiveData<Event<Boolean>> = _regDiary
    val _deleteDiary = MutableLiveData<Event<Boolean>>()
    val deleteDiary: LiveData<Event<Boolean>> = _deleteDiary

    val _focusPosition = MutableLiveData<Event<Int>>()
    val  focusPosition: LiveData<Event<Int>> = _focusPosition

//    fun initload(page_pos: Int): Flow<PagingData<Diary>> {
//        Log.e("BpData", "page_pos: $page_pos")
//        return Pager(
//            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
//            pagingSourceFactory = {
//                //BpDataPaingSource(service, query)
//                DiaryDetailPagingSource(page_pos)
//            }
//        ).flow
//    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }

    suspend fun deleteDiary(request: DeleteDiaryRequest) =
        withContext(Dispatchers.IO){
            val result = SulungApiService.instance.deleteDiary(request.id)
            if(result.isSuccessful)
                _deleteDiary.postValue(Event(true))
            else
                _deleteDiary.postValue(Event(false))
            result
        }
    suspend fun regDiaryEasily(request: DiaryEasyRegRequest) =
        withContext(Dispatchers.IO){
            val result = SulungApiService.instance.regEasyDiary(request)
            if(result.isSuccessful)
                _regDiary.postValue(Event(true))
            else
                _regDiary.postValue(Event(false))
            result
        }

    suspend fun regDiaryDetail(request: DiaryDetailRegRequest) =
        withContext(Dispatchers.IO){
            val result = SulungApiService.instance.regDetailDiary(
                content = request.content,
                diaryDt = request.diaryDt,
                drinkCount = request.drinkCount,
                drinkUnit = request.drinkUnit,
                drinkName = request.drinkName,
                drinkType = request.drinkType,
                emotion = request.emotion,
                id = request.id,
                imageFiles = request.imageFiles,
                tag = request.tag
            )
            if(result.isSuccessful)
                _regDiary.postValue(Event(true))
            else
                _regDiary.postValue(Event(false))

            result
        }

/*    suspend fun regDiaryDetail(request: DiaryDetailRegRequest) =
        withContext(Dispatchers.IO){
            val result = SulungApiService.instance.regDetailDiary(
                content = request.content.replace("\"", ""),
                diaryDt = request.diaryDt,
                drinkCount = request.drinkCount,
                drinkUnit = request.drinkUnit.replace("\"", ""),
                drinkName = request.drinkName.replace("\"", ""),
                drinkType = request.drinkType,
                emotion = request.emotion,
                id = request.id,
                imageFiles = request.imageFiles,
                tag = request.tag.replace("\"", "")
            )
            result
        }*/
    suspend fun getStoreListInfo(request: StoreListRequest) =
        withContext(Dispatchers.IO){
            val result = SulungApiService.instance.storeList(
                drinkType = request.drinkType,
                emotion = request.emotion,
                preCursor = request.preCursor,
                nextCursor = request.nextCursor,
                size = request.size
            )
            SulungDatabase.databaseWriteExecutor.execute(){
                try{
                    sulungDatabase.sulungDao.deleteStoreList()
                    sulungDatabase.sulungDao.insertStoreList(result.body()!!)
                }catch (e: Exception){ }
            }
            result
        }

    suspend fun getStoreListInfo2(request: StoreListArrayRequest) =
        withContext(Dispatchers.IO){
            val result = SulungApiService.instance.storeListArray(
                drinkType = request.drinkType,
                emotion = request.emotion,
                preCursor = request.preCursor,
                nextCursor = request.nextCursor,
                size = request.size
            )
            SulungDatabase.databaseWriteExecutor.execute(){
                try{
                    sulungDatabase.sulungDao.deleteStoreList()
                    sulungDatabase.sulungDao.insertStoreList(result.body()!!)
                }catch (e: Exception){ }
            }
            result
        }

    suspend fun getDiaryDetailInfo(request: DiaryDetailRequest) =
        withContext(Dispatchers.IO){
            val result = SulungApiService.instance.diaryDetail(request.id!!)
            SulungDatabase.databaseWriteExecutor.execute(){
                try{
                    sulungDatabase.sulungDao.deleteDiaryDetail()
                    sulungDatabase.sulungDao.insertDiaryDetail(result.body()!!)
                }catch (e: Exception){ }
            }
            result
        }
    suspend fun getDiaryListFirstPageInfo(request: DiaryListFirstPageRequest) =
        withContext(Dispatchers.IO){
            val result = SulungApiService.instance.diaryListFirstPage()
            SulungDatabase.databaseWriteExecutor.execute(){
                try{
                    sulungDatabase.sulungDao.deleteDiaryList()
                    sulungDatabase.sulungDao.insertDiaryList(result.body()!!)
                }catch (e: Exception){ }
            }
            result
        }

    /**
     * @author : ysp
     * @desc : 창고 월별 조회
     * @date : 04 23 2023
     */
    suspend fun getStoreListMonthly(request : StoreListMonthlyRequest) =
        withContext(Dispatchers.IO){
            val result = SulungApiService.instance.store_list_monthly(drinkType = request.drinkType, emotion = request.emotion, date = request.date, size = request.size)
            result
        }

    suspend fun getDiaryListMonthInfo(request : DiaryMonthlyRequest) =
        withContext(Dispatchers.IO){
            val result = SulungApiService.instance.diary_list_monthly(date = request.date, size = request.size, sort = request.sort)
            result
        }
    //val re = SulungApiService.instance.diary_year_month()
    /*suspend fun getDiaryListYearMonthInfo() =
        withContext(Dispatchers.IO){
            val result = SulungApiService.instance.diary_year_month()
            SulungDatabase.databaseWriteExecutor.execute(){
                try{
                    sulungDatabase.sulungDao.deleteDiaryListYearMonth()
                    sulungDatabase.sulungDao.insertDiaryListYearMonth(result.body()!!)
                }catch (e: Exception){ }
            }
            result
        }*/
    suspend fun getDiaryListYearMonthInfo() =
        withContext(Dispatchers.IO){
            val result = SulungApiService.instance.diary_year_month()
            val distinctItems = result.body()?.items?.distinct()

            SulungDatabase.databaseWriteExecutor.execute(){
                try{
                    sulungDatabase.sulungDao.deleteDiaryListYearMonth()
                    if (distinctItems != null) {
                        val distinctResponse = ReadDiaryYearMonthResponse(result.body()?.id ?: 0, distinctItems)
                        sulungDatabase.sulungDao.insertDiaryListYearMonth(distinctResponse)
                    }
                }catch (e: Exception){ }
            }
            result
        }


    suspend fun getDiaryListInfo(request: DiaryListRequest) =
        withContext(Dispatchers.IO){
            val result = SulungApiService.instance.diaryList(
                preCursor = request.preCursor,
                nextCursor = request.nextCursor,
                size = request.size!!,
                sort = request.sort!!
            )
            SulungDatabase.databaseWriteExecutor.execute(){
                try{
                    sulungDatabase.sulungDao.deleteDiaryList()
                    sulungDatabase.sulungDao.insertDiaryList(result.body()!!)
                }catch (e: Exception){ }
            }
            result
        }
}
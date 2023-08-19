package com.rummy.sulung.view.ui.diary.paging_another

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.paging.*
import com.rummy.sulung.common.App
import com.rummy.sulung.database.SulungDatabase
import com.rummy.sulung.network.repository.SulungRepository
import com.rummy.sulung.network.request.DiaryListRequest
import com.rummy.sulung.network.request.DiaryMonthlyRequest
import com.rummy.sulung.network.response.*
import com.rummy.sulung.view.ui.diary.paging.DiaryDetailSourceFactory
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class ReadDiaryListResponse(
    val allDiaries: List<DisplayableItem>
)

class PagingAndroidViewModel(application: Application) : ViewModel() {
    companion object {
        private const val PAGE_SIZE = 80
    }

    private val repository = App.repository

    //var diarys: LiveData<PagedList<DisplayableItem>>
    //var diarys: MutableLiveData<PagedList<DisplayableItem>> = MutableLiveData()

    private lateinit var diaryDetailSourceFactory: DiaryDetailSourceFactory

    private var _diarys: MutableLiveData<PagedList<DisplayableItem>> = MutableLiveData()
    val diarys: LiveData<PagedList<DisplayableItem>> by lazy { _diarys }

    private val _sort = MutableLiveData<String>().apply { value = "NEWEST" }
    val sort: LiveData<String> = _sort

    private val _focusPosition = MutableLiveData<Int>()
    val focusPosition: LiveData<Int> = _focusPosition

    lateinit var diaryYearMonthResponse: LiveData<ReadDiaryYearMonthResponse>

    init {
        diaryYearMonthResponse = repository.diaryYearMonthResponse

        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(PAGE_SIZE)
            .build()

        val factory = DiaryDetailSourceFactory(
            { callback -> loadInitial(callback, _sort.value!!) },
            { params, callback -> loadAfter(params, callback, _sort.value!!) })

        val initialDiarys = LivePagedListBuilder(factory, config).build()
        initialDiarys.observeForever { pagedList ->
            _diarys.postValue(pagedList)
        }
    }

    fun refresh() {
        /*_sort.postValue("NEWEST")
        _focusPosition.postValue(0)*/
        diarys.value?.dataSource?.invalidate()
    }

    fun updateSortOrder() {
        val newSort = if (_sort.value == "NEWEST") "OLDEST" else "NEWEST"
        _sort.value = newSort

        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(PAGE_SIZE)
            .build()

        val factory = DiaryDetailSourceFactory(
            { callback -> loadInitial(callback, _sort.value!!) },
            { params, callback -> loadAfter(params, callback, _sort.value!!) })

        diaryDetailSourceFactory = factory
        val newDiarys = LivePagedListBuilder(factory, config).build()
        var size = 0
        newDiarys.observeForever { pagedList ->
            if(pagedList.size > 0){
                _diarys.postValue(pagedList)
            }
        }
            diaryDetailSourceFactory.invalidate()
            _focusPosition.postValue(0)
    }

    fun loadDiaryYearMonth() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.getDiaryListYearMonthInfo()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadDiaryMonthlyList(request : DiaryMonthlyRequest) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val diaryListMonthlyRequest = DiaryMonthlyRequest(
                    date = request.date,
                    size = request.size,
                    sort = request.sort
                )
                val response = repository.getDiaryListMonthInfo(request)
                val result = response.body()
                val focusId = result?.focus

                // 포커싱을 위한 코드
                val focusPosition = diarys.value?.indexOfFirst { (it as? Diary)?.id == focusId }
                if (focusPosition != null && focusPosition != -1) {
                    withContext(Dispatchers.Main) {
                        _focusPosition.postValue(focusPosition - 1 ?: 0)
                    }
                }
            }
        }
    }


    private fun loadInitial(callback: PageKeyedDataSource.LoadInitialCallback<Int, DisplayableItem>, sort: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val request = DiaryListRequest(preCursor = null, nextCursor = null, size = PAGE_SIZE, sort = sort)
                val response = repository.getDiaryListInfo(request)
                val result = response.body()
                val items = result?.items ?: emptyList()
                val allDiaries = extractDiariesFromItems(items)
                callback.onResult(allDiaries, null, result?.nextCursor)
            }
        }
    }

    private fun loadAfter(
        params: PageKeyedDataSource.LoadParams<Int>,
        callback: PageKeyedDataSource.LoadCallback<Int, DisplayableItem>,
        sort: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val request = DiaryListRequest(preCursor = null, nextCursor = params.key, size = PAGE_SIZE, sort = sort)
                val response = repository.getDiaryListInfo(request)
                val result = response.body()
                val items = result?.items ?: emptyList()
                val allDiaries = extractDiariesFromItems(items)
                callback.onResult(allDiaries, result?.nextCursor)
            }
        }
    }

    private fun extractDiariesFromItems(items: List<Item>): List<DisplayableItem> {
        val storeList = mutableListOf<DisplayableItem>()
        items.forEach { item ->
            if (item.date != null) {
                storeList.add(DateSeparatorDiary(item.date))
            }
            item.diaries
                ?.filterNotNull()
                ?.let { storeList.addAll(it) }

            storeList.add(SpanCountItemDiary())
        }
        return storeList
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PagingAndroidViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PagingAndroidViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}

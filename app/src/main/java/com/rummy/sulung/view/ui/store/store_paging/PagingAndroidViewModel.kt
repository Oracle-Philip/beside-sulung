package com.rummy.sulung.view.ui.store.store_paging

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.paging.*
import com.rummy.sulung.common.App
import com.rummy.sulung.common.Event
import com.rummy.sulung.database.SulungDatabase
import com.rummy.sulung.network.repository.SulungRepository
import com.rummy.sulung.network.request.DiaryListRequest
import com.rummy.sulung.network.request.DiaryMonthlyRequest
import com.rummy.sulung.network.request.StoreListMonthlyRequest
import com.rummy.sulung.network.request.StoreListRequest
import com.rummy.sulung.network.response.*
import com.rummy.sulung.view.ui.diary.paging.DiaryDetailSourceFactory
import kotlinx.coroutines.*
import java.lang.Math.ceil
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class ReadStoreListResponse(
    val allStoreData: List<DisplayableItem>
)

@RequiresApi(Build.VERSION_CODES.O)
class PagingAndroidViewModel(application: Application) : ViewModel() {
    companion object {
        private const val PAGE_SIZE = 80
    }

    private val repository = App.repository

    var diarys: LiveData<PagedList<DisplayableItem>>

    lateinit var diaryYearMonthResponse: LiveData<ReadDiaryYearMonthResponse>

    lateinit var regDiary : LiveData<Event<Boolean>>
    lateinit var deleteDiary : LiveData<Event<Boolean>>

     val _focusPosition = MutableLiveData<Int>()
    var  focusPosition: LiveData<Int> = _focusPosition

    private val _currentFilter = MutableLiveData<Pair<String?, String?>>()
    val currentFilter: LiveData<Pair<String?, String?>> = _currentFilter


    //0611 2023
    private val _usingLoadDiaryMonthlyList = MutableLiveData<Boolean>(false)
    val usingLoadDiaryMonthlyList: LiveData<Boolean> get() = _usingLoadDiaryMonthlyList
    private val _dateLoadDiaryMonthlyList = MutableLiveData<String>()
    val dateLoadDiaryMonthlyList: LiveData<String> get() = _dateLoadDiaryMonthlyList

    init {
        regDiary = repository.regDiary
        deleteDiary = repository.deleteDiary

        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(PAGE_SIZE)
            .build()

        val factory = StoreSourceFactory(
            { callback -> loadInitial(_currentFilter.value?.first, _currentFilter.value?.second, _dateLoadDiaryMonthlyList.value, callback) },
            { params, callback -> loadAfter(_currentFilter.value?.first, _currentFilter.value?.second, _dateLoadDiaryMonthlyList.value, params, callback) })

        diarys = LivePagedListBuilder(factory, config).build()

        diaryYearMonthResponse = repository.diaryYearMonthResponse
    }

    fun updateFilter(drinkType: String?, emotion: String?) {
        _currentFilter.value = Pair(drinkType, emotion)
        diarys.value?.dataSource?.invalidate()
    }

    fun refresh() {
        _focusPosition.postValue(0)
        diarys.value?.dataSource?.invalidate()
    }

    fun loadDiaryYearMonth() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.getDiaryListYearMonthInfo()
            }
        }
    }

    private fun processItems(items: List<Item>): List<Item> {
        // 월별로 정렬
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
        val sortedItemsByMonth = items.sortedWith { item1, item2 ->
            val yearMonth1 = item1?.date?.let { YearMonth.parse(it, formatter) }
            val yearMonth2 = item2?.date?.let { YearMonth.parse(it, formatter) }
            when {
                yearMonth1 == null && yearMonth2 == null -> 0
                yearMonth1 == null -> 1
                yearMonth2 == null -> -1
                else -> yearMonth2.compareTo(yearMonth1)
            }
        }

        val combinedItems = mutableListOf<Item>()
        var currentItem: Item? = null

        sortedItemsByMonth.forEach { item ->
            if (currentItem?.date == item.date) {
                currentItem?.diaries = currentItem?.diaries?.plus(item.diaries.orEmpty())?.toMutableList()
            } else {
                currentItem?.let { combinedItems.add(it) }
                currentItem = item.copy()
            }

            // 일자별 정렬 추가
            currentItem?.diaries = currentItem?.diaries?.sortedWith { diary1, diary2 ->
                val date1 = diary1?.diaryDt?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
                val date2 = diary2?.diaryDt?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
                when {
                    date1 == null && date2 == null -> 0
                    date1 == null -> 1
                    date2 == null -> -1
                    else -> date1.compareTo(date2)
                }
            }?.toMutableList()
        }

        currentItem?.let { combinedItems.add(it) }
        return combinedItems
    }

    fun setUsingLoadDiaryMonthlyList(date: String) {
        _usingLoadDiaryMonthlyList.value = true
        _dateLoadDiaryMonthlyList.value = date
        diarys.value?.dataSource?.invalidate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadDiaryMonthlyList(request : StoreListMonthlyRequest) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val storeListMonthlyRequest = StoreListMonthlyRequest(
                    drinkType = request.drinkType,
                    emotion = request.emotion,
                    date = request.date,
                    size = request.size
                )
                val response = repository.getStoreListMonthly(storeListMonthlyRequest)
                val result = response.body()
                val focusId = result?.focus

                // 포커싱을 위한 코드
                val focusPosition = diarys.value?.indexOfFirst { (it as? Diary)?.id == focusId }
                if (focusPosition != null && focusPosition != -1) {
                    _focusPosition.postValue(focusPosition ?: 0)
                }
            }
        }
    }

    private fun extractStoreDiariesFromItems(items: List<Item>): List<DisplayableItem> {
        val storeList = mutableListOf<DisplayableItem>()
        items.forEach { item ->
            if (item.date != null) {
                storeList.add(DateSeparator(item.date))
            }
            item.diaries
                ?.filterNotNull()
                ?.let { storeList.addAll(it) }
            /**
             * @author ysp
             * @date 04 26 2023
             * @desc grid layout에 배치할시 빈 아이템을 통해 spancount 대체
             */
            // Calculate the remaining space for SpanCountItem
            val numberOfDiaries = (item.diaries?.filterNotNull()?.size ?: 0) + 1 // Add 1 for DateSeparator
            val spanCount = 5
            val rows = kotlin.math.ceil(numberOfDiaries / spanCount.toDouble()).toInt()
            val itemsInLastRow = numberOfDiaries % spanCount

            // Add SpanCountItem instances to fill the remaining space
            if (itemsInLastRow > 0) {
                val remainingSpace = spanCount - itemsInLastRow
                repeat(remainingSpace) {
                    storeList.add(SpanCountItem())
                }
            }
        }
        return storeList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadInitial(
        drinkType: String?,
        emotion: String?,
        date: String?,
        callback: PageKeyedDataSource.LoadInitialCallback<Int, DisplayableItem>
    ) {
        // Check if we should use loadDiaryMonthlyList()
        if (_usingLoadDiaryMonthlyList.value == false) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val request = StoreListRequest(
                        drinkType,
                        emotion,
                        preCursor = null,
                        nextCursor = null,
                        size = PAGE_SIZE
                    )
                    val response = repository.getStoreListInfo(request)
                    val result = response.body()
                    val items = result?.items ?: emptyList()
                    val processedItems = processItems(items as List<Item>) // 수정된 부분
                    val allDiaries = extractStoreDiariesFromItems(processedItems)

                    callback.onResult(allDiaries, null, result?.nextCursor)
                }
            }
        }else {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val storeListMonthlyRequest = StoreListMonthlyRequest(
                        drinkType = drinkType,
                        emotion = emotion,
                        date = date!!,
                        size = PAGE_SIZE
                    )
                    val response = repository.getStoreListMonthly(storeListMonthlyRequest)
                    val result = response.body()
                    val items = result?.items ?: emptyList()
                    val processedItems = processItems(items as List<Item>) // 수정된 부분
                    val allDiaries = extractStoreDiariesFromItems(processedItems)
                    val focusId = result?.focus

                    // 포커싱을 위한 코드
                    val focusPosition = diarys.value?.indexOfFirst { (it as? Diary)?.id == focusId }
                    if (focusPosition != null && focusPosition != -1) {
                        _focusPosition.postValue(focusPosition ?: 0)
                    }

                    callback.onResult(allDiaries, null, result?.nextCursor)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadAfter(
        drinkType: String?,
        emotion: String?,
        date: String?,
        params: PageKeyedDataSource.LoadParams<Int>,
        callback: PageKeyedDataSource.LoadCallback<Int, DisplayableItem>,
    ) {
        if (_usingLoadDiaryMonthlyList.value == false) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val request = StoreListRequest(
                        drinkType,
                        emotion,
                        preCursor = null,
                        nextCursor = params.key,
                        size = PAGE_SIZE
                    )
                    val response = repository.getStoreListInfo(request)
                    val result = response.body()
                    val items = result?.items ?: emptyList()

                    if (items.isEmpty()) {
                        return@withContext
                    }

                    if (result?.nextCursor == params.key) {
                        return@withContext
                    }

                    val processedItems = processItems(items as List<Item>) // 수정된 부분
                    val allStoreData = extractStoreDiariesFromItems(processedItems)

                    val isNextCursorInDiaries = items.flatMap { it?.diaries ?: emptyList() }
                        .any { it.id == result?.nextCursor }
                    if (!isNextCursorInDiaries) {
                        callback.onResult(allStoreData, result?.nextCursor)
                    }
                }
            }
        } else {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val storeListMonthlyRequest = StoreListMonthlyRequest(
                        drinkType = drinkType,
                        emotion = emotion,
                        date = date!!,
                        size = PAGE_SIZE
                    )
                    val response = repository.getStoreListMonthly(storeListMonthlyRequest)
                    val result = response.body()
                    val items = result?.items ?: emptyList()
                    val processedItems = processItems(items as List<Item>) // 수정된 부분
                    val allDiaries = extractStoreDiariesFromItems(processedItems)
                    val focusId = result?.focus

                    // 포커싱을 위한 코드
                    val focusPosition = diarys.value?.indexOfFirst { (it as? Diary)?.id == focusId }
                    if (focusPosition != null && focusPosition != -1) {
                        _focusPosition.postValue(focusPosition ?: 0)
                    }

                    if (items.isEmpty()) {
                        return@withContext
                    }

                    if (result?.nextCursor == params.key) {
                        return@withContext
                    }

                    val allStoreData = extractStoreDiariesFromItems(processedItems)

                    val isNextCursorInDiaries = items.flatMap { it?.diaries ?: emptyList() }
                        .any { it.id == result?.nextCursor }
                    if (!isNextCursorInDiaries) {
                        callback.onResult(allStoreData, result?.nextCursor)
                    }
                }
            }
        }
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

package com.rummy.sulung.view.ui.diary.paging
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.rummy.sulung.network.response.Diary
import com.rummy.sulung.network.response.DisplayableItem
import com.rummy.sulung.network.response.Item
import com.rummy.sulung.view.ui.diary.paging_another.DiaryDetailPagingSource

class DiaryDetailSourceFactory(
    private val onLoadInitial: (callback: PageKeyedDataSource.LoadInitialCallback<Int, DisplayableItem>) -> Unit,
    private val onLoadAfter: (params: PageKeyedDataSource.LoadParams<Int>, callback: PageKeyedDataSource.LoadCallback<Int, DisplayableItem>) -> Unit
) : DataSource.Factory<Int, DisplayableItem>() {

    private val _sourceLiveData = MutableLiveData<DiaryDetailPagingSource>()

    override fun create(): DataSource<Int, DisplayableItem> {
        val source = DiaryDetailPagingSource(onLoadInitial, onLoadAfter)
        _sourceLiveData.postValue(source)
        return source
    }

    fun invalidate() {
        _sourceLiveData.value?.invalidate() ?: run {
            val source = DiaryDetailPagingSource(onLoadInitial, onLoadAfter)
            _sourceLiveData.postValue(source)
            source.invalidate()
        }
    }
}


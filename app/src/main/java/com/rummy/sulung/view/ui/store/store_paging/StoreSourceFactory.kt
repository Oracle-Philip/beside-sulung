package com.rummy.sulung.view.ui.store.store_paging
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.rummy.sulung.network.response.Diary
import com.rummy.sulung.network.response.DisplayableItem
import com.rummy.sulung.network.response.Item
import com.rummy.sulung.view.ui.diary.paging_another.DiaryDetailPagingSource

class StoreSourceFactory(
    private val onLoadInitial: (callback: PageKeyedDataSource.LoadInitialCallback<Int, DisplayableItem>) -> Unit,
    private val onLoadAfter: (params: PageKeyedDataSource.LoadParams<Int>, callback: PageKeyedDataSource.LoadCallback<Int, DisplayableItem>) -> Unit
) : DataSource.Factory<Int, DisplayableItem>() {
    override fun create(): DataSource<Int, DisplayableItem> {
        return StorePagingSource(onLoadInitial, onLoadAfter)
    }
}

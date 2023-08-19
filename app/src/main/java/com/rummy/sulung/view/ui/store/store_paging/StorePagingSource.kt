package com.rummy.sulung.view.ui.store.store_paging

import android.widget.Toast
import androidx.paging.PageKeyedDataSource
import com.rummy.sulung.common.App
import com.rummy.sulung.network.response.Diary
import com.rummy.sulung.network.response.DisplayableItem
import com.rummy.sulung.network.response.ReadDiaryListResponse
import com.rummy.sulung.network.response.ReadStoreListResponse
import retrofit2.Response


class StorePagingSource(
    private val onLoadInitial: (callback: LoadInitialCallback<Int, DisplayableItem>) -> Unit,
    private val onLoadAfter: (params: LoadParams<Int>, callback: LoadCallback<Int, DisplayableItem>) -> Unit
) : PageKeyedDataSource<Int, DisplayableItem>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, DisplayableItem>
    ) {
        onLoadInitial(callback)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, DisplayableItem>) {
        onLoadAfter(params, callback)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, DisplayableItem>) {
    }

    data class ApiResponse<T>(
        val success: Boolean,
        val data: T? = null,
        val message: String? = null
    ) {

        companion object {
            inline fun <reified T> error(message: String? = null) =
                ApiResponse(false, null as T?, message)
        }

    }

    private fun showErrorMessage(response: Response<ReadStoreListResponse>) {
        Toast.makeText(App.instance, "알 수 없는 오류 발생 ${response.message()}", Toast.LENGTH_SHORT).show()
    }
}
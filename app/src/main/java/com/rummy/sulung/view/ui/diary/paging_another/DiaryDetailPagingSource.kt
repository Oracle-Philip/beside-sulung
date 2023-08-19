package com.rummy.sulung.view.ui.diary.paging_another

import android.util.Log
import android.widget.Toast
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rummy.sulung.common.App
import com.rummy.sulung.common.App.Companion.TAG
import com.rummy.sulung.network.repository.SulungRepository.Companion.NETWORK_PAGE_SIZE
import com.rummy.sulung.network.request.DiaryDetailRequest
import com.rummy.sulung.network.request.DiaryListRequest
import com.rummy.sulung.network.request.ReadDiaryListResponse2
import com.rummy.sulung.network.response.*
import com.rummy.sulung.network.response.ReadDiaryListResponse
import com.rummy.sulung.network.service.SulungApiService
import kotlinx.coroutines.*
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


class DiaryDetailPagingSource(
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

    private fun showErrorMessage(response: Response<ReadDiaryListResponse>) {
        Toast.makeText(App.instance, "알 수 없는 오류 발생 ${response.message()}", Toast.LENGTH_SHORT).show()
    }
}
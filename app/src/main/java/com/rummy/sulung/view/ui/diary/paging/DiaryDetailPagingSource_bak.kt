//package com.rummy.sulung.view.ui.diary.paging
//
//import android.util.Log
//import android.widget.Toast
//import androidx.paging.PageKeyedDataSource
//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import com.rummy.sulung.common.App
//import com.rummy.sulung.common.App.Companion.TAG
//import com.rummy.sulung.network.repository.SulungRepository.Companion.NETWORK_PAGE_SIZE
//import com.rummy.sulung.network.request.DiaryDetailRequest
//import com.rummy.sulung.network.request.DiaryListRequest
//import com.rummy.sulung.network.request.ReadDiaryListResponse2
//import com.rummy.sulung.network.response.Diary
//import com.rummy.sulung.network.response.Item
//import com.rummy.sulung.network.response.ReadDiaryDetailResponse
//import com.rummy.sulung.network.response.ReadDiaryListResponse
//import com.rummy.sulung.network.service.SulungApiService
//import kotlinx.coroutines.*
//import retrofit2.HttpException
//import retrofit2.Response
//import java.io.IOException
//
//
////218,"nextCursor":207
//
////oldest
////{"preCursor":5,"nextCursor":25
//private const val STARTING_KEY_NEWEST = 5
//private const val STARTING_KEY_OLDEST = 5
//private const val LOAD_DELAY_MILLIS = 3_000L
//private fun ensureValidKey(key: Int) = Integer.max(STARTING_KEY_NEWEST, key)
//
//class DiaryDetailPagingSource : PageKeyedDataSource<Int, Diary>() {
//    //    override suspend fun load(params: Params<Int>): BaseResult<Diary> {
////        val init_newest = SulungApiService.instance.diaryList(preCursor = null, nextCursor = null, size = 10, sort = "NEWEST").body()
////        val init_oldest = SulungApiService.instance.diaryList(preCursor = null, nextCursor = null, size = 10, sort = "OLDEST").body()
////
//////        val prePageNumber = params.key ?: init_newest?.preCursor
//////        val nextPageNumber = params.key ?: init_newest?.nextCursor
////        val start = params.key ?: init_newest?.preCursor
////        val range = start?.until(start + params.loadSize)
////
////        if(start != STARTING_KEY_NEWEST) delay(LOAD_DELAY_MILLIS)
////
////        return try {
////            val response = SulungApiService.instance.diaryList(preCursor = null, nextCursor = null, sort = "NEWEST")
////            val result = response.body()
////            LoadResult.Page(
////                data = result?.items?.get(0)?.diaries!!,
////                prevKey = when(start){
////                    STARTING_KEY_NEWEST -> null
////                    else -> ensureValidKey(key = range?.first?.minus(params.loadSize) ?: -1)
////                },
////                nextKey = range?.last?.plus(1)
////            )
////        } catch (exception: IOException) {
////            LoadResult.Error(exception)
////        } catch (exception: HttpException) {
////            LoadResult.Error(exception)
////        }
////    }
//
//    override fun loadInitial(
//        params: LoadInitialParams<Int>,
//        callback: LoadInitialCallback<Int, Diary>
//    ) {
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
//    }
//
//    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Diary>) {
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
//    }
//
//    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Diary>) {
//        val response = getDiary(Int.MAX_VALUE, PREV)
//        if(response.isSuccessful){
//            response.body()?.items?.get(0)?.diaries.let{
//                if(it?.isNotEmpty() == true)
//                    callback.onResult(it, it.first().id)
//            }
//        }else{
//            GlobalScope.launch(Dispatchers.Main) {
//                showErrorMessage(response)
//            }
//        }
//    }
//
//    private fun getDiary(page: Int, direction: String) : Response<ReadDiaryListResponse> = runBlocking {
//            var prev : Int? = if(direction == PREV) page else null
//            var next : Int? = if(direction == NEXT) page else null
//            val response = SulungApiService.instance.diaryList(preCursor = prev, nextCursor = next, size = 10, sort = "NEWEST")
//        try {
//
//        } catch (e: Exception) {
////            ApiResponse.error<List<ReadDiaryListResponse>>(
////                "알 수 없는 오류가 발생했습니다."
////            )
//            Toast.makeText(App.instance, "알 수 없는 오류 발생 : $e", Toast.LENGTH_SHORT).show()
//        }
//        response
//    }
//
//    companion object {
//        private const val NEXT = "next"
//        private const val PREV = "prev"
//    }
//
//    data class ApiResponse<T>(
//        val success: Boolean,
//        val data: T? = null,
//        val message: String? = null
//    ) {
//
//        companion object {
//            inline fun <reified T> error(message: String? = null) =
//                ApiResponse(false, null as T?, message)
//        }
//
//    }
//
//    private fun showErrorMessage(response: Response<ReadDiaryListResponse>) {
//        Toast.makeText(App.instance, "알 수 없는 오류 발생 ${response.message()}", Toast.LENGTH_SHORT).show()
//    }
////
////    override fun loadInitial(
////        params: LoadInitialParams<Int>,
////        callback: LoadInitialCallback<Int, Diary>
////    ) {
////        TODO("Not yet implemented")
////    }
//}

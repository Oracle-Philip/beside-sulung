/*
package com.rummy.sulung.view.ui.diary.paging

import android.util.Log
import android.widget.Toast
import androidx.paging.PageKeyedDataSource
import com.rummy.sulung.common.App
import com.rummy.sulung.common.App.Companion.TAG
import com.rummy.sulung.network.response.Diary
import com.rummy.sulung.network.response.ReadDiaryDetailResponse
import com.rummy.sulung.network.response.ReadDiaryListResponse
import com.rummy.sulung.network.service.SulungApiService
import kotlinx.coroutines.*
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


//218,"nextCursor":207

//oldest
//{"preCursor":5,"nextCursor":25
private const val STARTING_KEY_NEWEST = 5
private const val STARTING_KEY_OLDEST = 5
private const val LOAD_DELAY_MILLIS = 3_000L
private fun ensureValidKey(key: Int) = Integer.max(STARTING_KEY_NEWEST, key)

class DiaryDetailPagingSource(
    onLoadInitial: (callback: LoadInitialCallback<Int, Diary>) -> Unit,
    onLoadAfter: (params: LoadParams<Int>, callback: LoadCallback<Int, Diary>) -> Unit
) : PageKeyedDataSource<Int, Diary>() {

    var nextPage = 0
    var prevPage = 0

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Diary>
    ) {
//        onLoadInitial(callback)
        val response = getDiary(null, null)
        if(response.isSuccessful){
//            response.body()?.items?.get(0)?.diaries.let{
//                if(it?.isNotEmpty() == true)
//                    callback.onResult(it, it.first().id, it.last().id)
//            }
            response.body().let{

                val range = it?.items?.size?.let { it1 -> 0.until(it1) }
                try {
                    val range2 = it?.items?.get(0)?.diaries?.size?.let { it1 -> 0.until(it1) }
                    Log.e(TAG, "range2 ${range2?.first} ~ ${range2?.last}")
                }catch (e:Exception){

                }

                if(it != null) {
                    range?.map { number ->
                        it.items[number].diaries?.let { it1 ->
                            callback.onResult(
                                it1,
                                it.preCursor,
                                it.nextCursor
                            )
                        }
                        prevPage = it.preCursor!!
                        nextPage = it.nextCursor!!

                        Log.e(TAG, "prevPage $prevPage")
                        Log.e(TAG, "nextPage $nextPage")
                    }
                }
            }
        }else{
            GlobalScope.launch(Dispatchers.Main) {
                showErrorMessage(response)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Diary>) {
//        onLoadAfter(params, callback)

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

        val response = getDiary(nextPage, NEXT)
        if(response.isSuccessful){
//            response.body()?.items?.get(0)?.diaries.let{
//                if(it?.isNotEmpty() == true)
//                    callback.onResult(it, it.last().id)
//            }

//            if(it != null) {
//                it.items.map { number ->
//                    number.diaries?.let { it1 ->
//                        callback.onResult(
//                            it1,
//                            it.preCursor,
//                            it.nextCursor
//                        )
//                    }
//                    prevPage = it.preCursor!!
//                    nextPage = it.nextCursor!!
//
//                    Log.e(TAG, "prevPage $prevPage")
//                    Log.e(TAG, "nextPage $nextPage")
//                }
//            }

            response.body().let{
                val range = it?.items?.size?.let { it1 -> 0.until(it1) }
                try {
                    val range2 = it?.items?.get(0)?.diaries?.size?.let { it1 -> 0.until(it1) }
                    Log.e(TAG, "nextPage range2 ${range2?.first} ~ ${range2?.last}")
                }catch (e:Exception){

                }
                if(it != null) {
                    try {
                        range?.map { number ->
                            it.items[number].diaries?.let{ it1 ->
                                callback.onResult(
                                    it1,
                                    it.nextCursor
                                )
                            }
                            prevPage = it.preCursor!!
                            nextPage = it.nextCursor!!
                        }
                    }catch (e: Exception){
                        //Toast.makeText(App.instance, "error $e", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "nextPage error : $e")
                    }
                }
            }
        }else{
            GlobalScope.launch(Dispatchers.Main) {
                showErrorMessage(response)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Diary>) {
        val response = getDiary(prevPage, PREV)
        if(response.isSuccessful){
            */
/*         response.body()?.items?.get(0)?.diaries.let{
                         if(it?.isNotEmpty() == true)
                             callback.onResult(it, it.first().id)
                     }*//*

            response.body().let{
                val range = it?.items?.size?.let { it1 -> 0.until(it1) }
                try {
                    val range2 = it?.items?.get(0)?.diaries?.size?.let { it1 -> 0.until(it1) }
                    Log.e(TAG, "PREV range2 ${range2?.first} ~ ${range2?.last}")
                }catch (e: Exception){

                }
*/
/*                if(it != null) {
                    try {
                        it.items.map{ number ->
                            number.diaries?.let{ it1 ->
                                callback.onResult(
                                    it1,
                                    it.nextCursor
                                )
                            }
                            prevPage = it.preCursor!!
                            nextPage = it.nextCursor!!
                        }
                    }catch (e: Exception){
                        //Toast.makeText(App.instance, "error $e", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "nextPage error : $e")
                    }
                }*//*

                if(it != null) {
                    try {
                        range?.map { number ->
                            it.items[number].diaries?.let{ it1 ->
                                callback.onResult(it1, it.preCursor)
                            }
                            prevPage = it.preCursor!!
                            nextPage = it.nextCursor!!
                        }
                    }catch (e: Exception){
                        //Toast.makeText(App.instance, "error $e", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "prevPage error : $e")
                    }
                }
            }
        }else{
            GlobalScope.launch(Dispatchers.Main) {
                showErrorMessage(response)
            }
        }
    }

    private fun getDiary(page: Int?, direction: String?) : Response<ReadDiaryListResponse> = runBlocking {
        var prev : Int? = if(direction == PREV) page else null
        var next : Int? = if(direction == NEXT) page else null
        val response = SulungApiService.instance.diaryList(preCursor = prev, nextCursor = next, size = 10, sort = "NEWEST")
        try {

        } catch (e: Exception) {
            Toast.makeText(App.instance, "알 수 없는 오류 발생 : $e", Toast.LENGTH_SHORT).show()
        }
        response
    }

    companion object {
        private const val NEXT = "next"
        private const val PREV = "prev"
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
}*/

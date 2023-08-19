/*
package com.rummy.sulung.view.ui.diary.paging

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import androidx.savedstate.SavedStateRegistryOwner
import com.rummy.sulung.network.response.Diary

class PagingAndroidViewModel_bak(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application), DiaryAdapter.DiaryLiveDataBuilder, DiaryAdapter.OnItemClickListener {

    override fun buildPagedList(): LiveData<PagedList<Diary>> {
        return super.buildPagedList()
    }

    override fun onClickDiary(diaryId: Long?) {
        TODO("Not yet implemented")
    }

    override fun createDataSource(): DataSource<Int, Diary> {
//        if (categoryId == -1)
//            error(
//                "categoryId가 설정되지 않았습니다.",
//                IllegalStateException("categoryId is -1")
//            )
        return DiaryDetailPagingSource(onLoadInitial, onLoadAfter)
    }

    class Factory(
        val app: Application,
        owner: SavedStateRegistryOwner,
    ) : AbstractSavedStateViewModelFactory(owner, null) {

        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            if (modelClass.isAssignableFrom(PagingAndroidViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PagingAndroidViewModel(app, handle) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}*/

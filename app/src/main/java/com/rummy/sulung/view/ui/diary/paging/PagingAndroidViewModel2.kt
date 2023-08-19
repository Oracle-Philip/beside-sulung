//package com.rummy.sulung.view.ui.diary.paging
//
//import android.app.Application
//import androidx.lifecycle.*
//import androidx.paging.PagingData
//import androidx.paging.cachedIn
//import androidx.savedstate.SavedStateRegistryOwner
//import com.rummy.sulung.database.SulungDatabase
//import com.rummy.sulung.network.repository.SulungRepository
//import com.rummy.sulung.network.response.Diary
//import com.rummy.sulung.network.response.Item
//import com.rummy.sulung.network.response.ReadDiaryDetailResponse
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//
//class PagingAndroidViewModel(
//    application: Application,
//    private val savedStateHandle: SavedStateHandle
//) : AndroidViewModel(application) {
//
//    /**
//     * Stream of immutable states representative of the UI.
//     */
//    val state: StateFlow<UiState>
//    val pagingDataFlow: Flow<PagingData<Diary>>
//    val accept: (UiAction) -> Unit
//    val sulungRepository = SulungRepository(SulungDatabase.getDatabase(application)!!)
//
//    init {
//        val initialQuery: String = savedStateHandle.get(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
//        val lastQueryScrolled: String = savedStateHandle.get(LAST_QUERY_SCROLLED) ?: DEFAULT_QUERY
//        val actionStateFlow = MutableSharedFlow<UiAction>()
//        val searches = actionStateFlow
//            .filterIsInstance<UiAction.Search>()
//            .distinctUntilChanged()
//            .onStart { emit(UiAction.Search(query = initialQuery)) }
//        val queriesScrolled = actionStateFlow
//            .filterIsInstance<UiAction.Scroll>()
//            .distinctUntilChanged()
//            // This is shared to keep the flow "hot" while caching the last query scrolled,
//            // otherwise each flatMapLatest invocation would lose the last query scrolled,
//            .shareIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
//                replay = 1
//            )
//            .onStart { emit(UiAction.Scroll(currentQuery = lastQueryScrolled)) }
//
//        pagingDataFlow = searches
//            .flatMapLatest {
//                //searchRepo(queryString = it.query)
//                initload(208)
//            }
//            .cachedIn(viewModelScope)
//
//        state = combine(
//            searches,
//            queriesScrolled,
//            ::Pair
//        ).map { (search, scroll) ->
//            UiState(
//                query = search.query,
//                lastQueryScrolled = scroll.currentQuery,
//                // If the search query matches the scroll query, the user has scrolled
//                hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery
//            )
//        }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
//                initialValue = UiState()
//            )
//        accept =
//            {
//                    action ->
//                viewModelScope.launch { actionStateFlow.emit(action) }
//            }
//    }
//
//    override fun onCleared() {
//        savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
//        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
//        super.onCleared()
//    }
//
//    private fun initload(page_pos: Int): Flow<PagingData<Diary>> =
//        sulungRepository.initload(page_pos)
//
//    class Factory(
//        val app: Application,
//        owner: SavedStateRegistryOwner,
//    ) : AbstractSavedStateViewModelFactory(owner, null) {
//
//        override fun <T : ViewModel?> create(
//            key: String,
//            modelClass: Class<T>,
//            handle: SavedStateHandle
//        ): T {
//            if (modelClass.isAssignableFrom(PagingAndroidViewModel::class.java)) {
//                @Suppress("UNCHECKED_CAST")
//                return PagingAndroidViewModel(app, handle) as T
//            }
//            throw IllegalArgumentException("Unknown ViewModel class")
//        }
//    }
//}
//
//
////    private fun searchRepo(queryString: String): Flow<PagingData<Repo>> =
////        repository.getSearchResultStream(queryString)
//
//
////    class Factory(val app: Application) : ViewModelProvider.Factory {
////        override fun <T : ViewModel> create(modelClass: Class<T>): T {
////            if (modelClass.isAssignableFrom(PagingAndroidViewModel::class.java)) {
////                @Suppress("UNCHECKED_CAST")
////                return PagingAndroidViewModel(app) as T
////            }
////            throw IllegalArgumentException("Unable to construct viewmodel")
////        }
////    }
//
//sealed class UiAction {
//    data class Search(val query: String) : UiAction()
//    //data class Scroll(val currentQuery: String) : UiAction()
//    data class Scroll(val currentQuery: String) : UiAction()
//}
//
//data class UiState(
//    val query: String = DEFAULT_QUERY,
//    val lastQueryScrolled: String = DEFAULT_QUERY,
//    val hasNotScrolledForCurrentSearch: Boolean = false
//)
//
//private const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"
//private const val LAST_SEARCH_QUERY: String = "last_search_query"
//private const val DEFAULT_QUERY = "Android"
package com.rummy.sulung.view.ui.store

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {
    private val _filterDrinkType = MutableLiveData<String>()
    val filterDrinkType: LiveData<String> get() = _filterDrinkType

    private val _filterEmotion = MutableLiveData<String>()
    val filterEmotion: LiveData<String> get() = _filterEmotion

    private val _selectedFilters = MutableLiveData<String?>()
    val selectedFilters: LiveData<String?> get() = _selectedFilters

    private val _filterCount = MutableLiveData<Int?>()
    val filterCount: LiveData<Int?> get() = _filterCount

    fun setFilterDrinkType(drinkType: String) {
        viewModelScope.launch {
            _filterDrinkType.postValue(drinkType)
        }
    }

    fun setFilterEmotion(emotion: String) {
        viewModelScope.launch {
            _filterEmotion.postValue(emotion)
        }
    }

    fun updateSelectedFilters(filters: String?) {
        viewModelScope.launch {
            _selectedFilters.postValue(filters)
        }
    }

    fun updateFilterCount(count: Int?) {
        viewModelScope.launch {
            _filterCount.postValue(count)
        }
    }
}


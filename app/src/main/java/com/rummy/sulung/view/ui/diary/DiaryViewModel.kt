package com.rummy.sulung.view.ui.diary

import android.app.Application
import androidx.lifecycle.*

class DiaryViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}
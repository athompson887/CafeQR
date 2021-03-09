package com.athompson.cafe.customer.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.athompson.cafe.customer.Enums

class HomeViewModel : ViewModel() {

    fun setMode(md: Enums.HomeScreenMode) {
        _mode.postValue(md)
    }
    private val _mode = MutableLiveData<Enums.HomeScreenMode>().apply {
        value = Enums.HomeScreenMode.WELCOME
    }
    var mode: LiveData<Enums.HomeScreenMode> = _mode


    private val _progressInfo = MutableLiveData<String>().apply {
        value = "Collection information"
    }
    val progressInfo: LiveData<String> = _progressInfo
}
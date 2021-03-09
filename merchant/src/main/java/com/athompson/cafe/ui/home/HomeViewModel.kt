package com.athompson.cafe.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.athompson.cafe.Enums
import com.google.firebase.firestore.DocumentSnapshot

class HomeViewModel() : ViewModel() {

    var userDocument: DocumentSnapshot? = null

    fun setMode(md: Enums.HomeScreenMode) {
        _mode.postValue(md)
    }
    private val _mode = MutableLiveData<Enums.HomeScreenMode>().apply {
        value = Enums.HomeScreenMode.LOGIN
    }
    var mode: LiveData<Enums.HomeScreenMode> = _mode

    fun setStatus(st: String) {
        _status.postValue(st)
    }
    private val _status = MutableLiveData<String>().apply {
        value = ""
    }
    var status: LiveData<String> = _status
}
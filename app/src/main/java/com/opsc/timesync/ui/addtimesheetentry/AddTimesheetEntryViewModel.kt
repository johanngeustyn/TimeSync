package com.opsc.timesync.ui.addtimesheetentry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddTimesheetEntryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Add Timesheet Fragment"
    }
    val text: LiveData<String> = _text
}
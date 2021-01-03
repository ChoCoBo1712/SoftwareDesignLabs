package com.example.tabatatimer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class SequenceDetailViewModel(
        var id: Int = 0,
        var title: String = "",
        var preparation: Int = 1,
        var workout: Int = 1,
        var rest: Int = 1,
        var cycles: Int = 1,
        var colour: Int = 0
) : ViewModel()


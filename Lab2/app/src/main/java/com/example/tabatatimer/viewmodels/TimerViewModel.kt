package com.example.tabatatimer.viewmodels

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel

data class TimerViewModel(
    var title: String = "",
    var preparation: Int = 1,
    var workout: Int = 1,
    var rest: Int = 1,
    var cycles: Int = 1,
    var colour: Int = 0
) : ViewModel()

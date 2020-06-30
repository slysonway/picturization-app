package com.esgi.picturization.ui.home.treated

interface TreatedListener {
    fun onStarted()

    fun onFinish()

    fun onError(message: String)

    fun onSuccess()
}
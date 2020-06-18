package com.esgi.picturization.ui.home.start

interface StartListener {

    fun onStarted()

    fun onFinish()

    fun onSuccess()

    fun onError(message: String)
}
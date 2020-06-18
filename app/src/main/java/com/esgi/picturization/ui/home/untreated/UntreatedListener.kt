package com.esgi.picturization.ui.home.untreated

interface UntreatedListener {

    fun onStarted()

    fun onFinish()

    fun onError(message: String)

    fun onSuccess()
}
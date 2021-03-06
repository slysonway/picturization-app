package com.esgi.picturization.ui.home.image.transform

interface TransformListener {

    fun onStarted()

    fun onFinish()

    fun onError(message: String)

    fun onSuccess()
}
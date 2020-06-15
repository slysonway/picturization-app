package com.esgi.picturization.ui.home.transform

interface TransformListener {

    fun onStarted()

    fun onFinish()

    fun onError()

    fun onSuccess()
}
package com.esgi.picturization.ui.auth

import androidx.lifecycle.LiveData
import com.esgi.picturization.data.db.entities.User

interface AuthListener {

    /**
     * on Started
     */
    fun onStarted()

    /**
     * on Success
     */
    fun onSuccess(user: User)

    /**
     * on Failure
     * @param message [String]
     */
    fun onFailure(message: String)

    /**
     * on Finish
     */
    fun onFinish()

}
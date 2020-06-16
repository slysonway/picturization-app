package com.esgi.picturization.ui.home.profile

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.esgi.picturization.data.repositories.ImageRepository
import com.esgi.picturization.data.repositories.UserRepository
import com.esgi.picturization.util.Coroutines
import java.io.File
import java.io.IOException
import java.lang.Exception

class ProfileViewModel(
    private val repository: UserRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
    val user = repository.getUser()

//    fun sendImage(data: File) {
//        Coroutines.main {
//            try {
//                val t = imageRepository.sendImage(data)
//                Log.d(this::class.java.simpleName, t)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//
//        }
//    }
}

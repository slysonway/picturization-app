package com.esgi.picturization.ui.home.image.details

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.esgi.picturization.data.models.DbImage
import com.esgi.picturization.data.repositories.ImageRepository
import com.esgi.picturization.util.Coroutines
import okhttp3.*
import java.io.IOException

class ImageDetailsViewModel(
    private val imageRepository: ImageRepository
) : ViewModel() {

    lateinit var image: DbImage

    fun onDownloadButtonClick(v: View) {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(image.url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(this::class.java.simpleName, "request failed: " + e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val a = response.body()!!.byteStream()
            }
        })
//        Coroutines.main {
//            val a = imageRepository.downloadImage(image.url)
//            Log.d(this::class.java.simpleName, a.toString())
//        }
    }

}
package com.esgi.picturization.data.repositories

import android.content.Intent
import com.esgi.picturization.data.network.ImageApi
import com.esgi.picturization.data.network.SafeApiRequest
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class ImageRepository(
    private val api: ImageApi
) : SafeApiRequest() {

    suspend fun sendImage(file: File) : String {
        val requestFile: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), file)

        // MultipartBody.Part is used to send also the actual file name

        // MultipartBody.Part is used to send also the actual file name
        val body =
            MultipartBody.Part.createFormData("image", file.getName(), requestFile)

        // add another part within the multipart request

        // add another part within the multipart request
//        val fullName =
//            RequestBody.create(MediaType.parse("multipart/form-data"), "Your Name")

        return apiRequest { api.sendImage(body) }
    }
}
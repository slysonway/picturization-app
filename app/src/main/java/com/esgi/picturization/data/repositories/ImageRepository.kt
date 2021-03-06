package com.esgi.picturization.data.repositories

import com.esgi.picturization.data.models.DbImage
import com.esgi.picturization.data.models.Image
import com.esgi.picturization.data.models.UrlImage
import com.esgi.picturization.data.network.SafeApiRequest
import com.esgi.picturization.data.network.api.ImageApi
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody


class ImageRepository(
    private val api: ImageApi
) : SafeApiRequest() {

    suspend fun sendImage(image: Image) : UrlImage {
        val file = Gson().toJson(image.filters)
        val filters =
            RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val requestFile: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), image.file)
        val imageBody =
            MultipartBody.Part.createFormData("image", image.file.name, requestFile)

        return apiRequest { api.sendImage(imageBody, filters) }
    }

    suspend fun getUntreatedImage() : List<DbImage> {
        return apiRequest { api.getUntreatedImage() }
    }

    suspend fun getTreatedImage() : List<DbImage> {
        return apiRequest { api.getTreatedImage() }
    }

    suspend fun getAllImage() : List<DbImage> {
        return apiRequest { api.getAllImage() }
    }
}
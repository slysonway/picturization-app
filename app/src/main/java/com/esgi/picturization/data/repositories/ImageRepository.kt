package com.esgi.picturization.data.repositories

import com.esgi.picturization.data.models.DbImage
import com.esgi.picturization.data.models.Image
import com.esgi.picturization.data.models.UrlImage
import com.esgi.picturization.data.network.ImageApi
import com.esgi.picturization.data.network.SafeApiRequest
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Url
import java.lang.StringBuilder
import java.net.URL


class ImageRepository(
    private val api: ImageApi
) : SafeApiRequest() {

    suspend fun sendImage(image: Image) : UrlImage {
        val f = image.filters.joinToString(",")
        val filters =
            RequestBody.create(MediaType.parse("multipart/form-data"), f)
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
}
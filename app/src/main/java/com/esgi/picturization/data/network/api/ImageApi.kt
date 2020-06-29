package com.esgi.picturization.data.network.api

import com.esgi.picturization.data.models.DbImage
import com.esgi.picturization.data.models.UrlImage
import com.esgi.picturization.data.network.interceptor.AuthenticationInterceptorRefreshToken
import com.esgi.picturization.data.network.interceptor.NetworkConnectionInterceptor
import com.esgi.picturization.util.Constants
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ImageApi {

    @Multipart
    @POST("images/untreated")
    suspend fun sendImage(@Part image: MultipartBody.Part, @Part("jsonFilters") filters: RequestBody) : Response<UrlImage>

    @GET("images/untreated")
    suspend fun getUntreatedImage() : Response<List<DbImage>>

    @GET("images/treated")
    suspend fun getTreatedImage() : Response<List<DbImage>>

    @GET("{url}")
    suspend fun downloadImage(@Path("url") url: String) : Response<ResponseBody>

    companion object {
        operator fun invoke(
            networkConnectionInterceptor: NetworkConnectionInterceptor,
            authenticationInterceptorRefreshToken: AuthenticationInterceptorRefreshToken
        ) : ImageApi {
            val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
            }
            val dispatcher = Dispatcher()
            dispatcher.maxRequests = 1
            val okHttpClient = OkHttpClient.Builder().apply {
                this.callTimeout(60, TimeUnit.SECONDS)
                this.readTimeout(60, TimeUnit.SECONDS)
                this.connectTimeout(60, TimeUnit.SECONDS)
                this.dispatcher(dispatcher)
                this.addInterceptor(interceptor)
                this.addInterceptor(authenticationInterceptorRefreshToken)
                this.addInterceptor(networkConnectionInterceptor)
                this.cache(null)
            }.build()


            //val gsonBuiler = GsonBuilder()
            //gsonBuilder.registerTypeAdapter(Date::class.java, DateDeserializer())

            val gson = GsonBuilder().setLenient().create()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ImageApi::class.java)
        }
    }
}
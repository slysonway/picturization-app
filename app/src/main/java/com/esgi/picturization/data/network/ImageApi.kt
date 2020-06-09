package com.esgi.picturization.data.network

import com.esgi.picturization.util.Constants
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit

interface ImageApi {

    @Multipart
    @POST("images")
    suspend fun sendImage(@Part image: MultipartBody.Part) : Response<String>


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
            }.build()

            //val gsonBuiler = GsonBuilder()
            //gsonBuilder.registerTypeAdapter(Date::class.java, DateDeserializer())
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ImageApi::class.java)
        }
    }
}
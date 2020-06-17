package com.esgi.picturization.data.network.api

import com.esgi.picturization.data.models.UserLogin
import com.esgi.picturization.data.network.interceptor.NetworkConnectionInterceptor
import com.esgi.picturization.data.network.responses.AuthResponse
import com.esgi.picturization.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface AuthApi {

    @POST("login")
    suspend fun userLogin(
        @Body userLogin: UserLogin
    ) : Response<AuthResponse>

    @FormUrlEncoded
    @POST("register")
    suspend fun userSignup(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<AuthResponse>


    companion object {
        operator fun invoke(
            networkConnectionInterceptor: NetworkConnectionInterceptor
        ) : AuthApi {

            val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
            }

            val okkHttpClient = OkHttpClient.Builder().apply {
                this.callTimeout(60, TimeUnit.SECONDS)
                this.readTimeout(60, TimeUnit.SECONDS)
                this.connectTimeout(60, TimeUnit.SECONDS)
                this.addInterceptor(networkConnectionInterceptor)
                this.addInterceptor(interceptor)
            }.build()

            return Retrofit.Builder()
                .client(okkHttpClient)
                .baseUrl(Constants.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthApi::class.java)
        }
    }
}
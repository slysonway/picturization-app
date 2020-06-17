package com.esgi.picturization.data.network.interceptor

import com.esgi.picturization.data.repositories.UserRepository
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthenticationInterceptorRefreshToken(private val userRepository: UserRepository) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        synchronized(this) {
            val originalRequest = chain.request()
            val authenticationRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer ${userRepository.getToken()}")
                .build()
            val initialResponse = chain.proceed(authenticationRequest)
            when {
                initialResponse.code() == 403 || initialResponse.code() == 401 -> {
                    return initialResponse
                }
                else -> return initialResponse
            }
        }
    }

}
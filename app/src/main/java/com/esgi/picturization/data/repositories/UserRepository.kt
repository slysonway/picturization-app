package com.esgi.picturization.data.repositories

import com.esgi.picturization.data.db.AppDatabase
import com.esgi.picturization.data.db.entities.User
import com.esgi.picturization.data.models.UserLogin
import com.esgi.picturization.data.models.UserRegister
import com.esgi.picturization.data.network.api.AuthApi
import com.esgi.picturization.data.network.SafeApiRequest
import com.esgi.picturization.data.network.responses.AuthResponse

class UserRepository(
    private val api: AuthApi,
    private val db: AppDatabase
) : SafeApiRequest() {

    suspend fun userLogin(email: String, password: String): AuthResponse {
        return apiRequest { api.userLogin(UserLogin(email, password)) }
    }

    suspend fun userSignup(name: String, email: String, password: String): Int {
        return apiRequest { api.userSignup(UserRegister(name, email, password)) }
    }

    suspend fun saveUser(user: User) = db.getUserDao().upsert(user)

    fun getUser() = db.getUserDao().getUser()

    fun getToken() = db.getUserDao().getToken()

}
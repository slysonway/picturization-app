package com.esgi.picturization.ui.auth

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.auth0.android.jwt.JWT
import com.esgi.picturization.data.db.entities.User
import com.esgi.picturization.data.repositories.UserRepository
import com.esgi.picturization.util.*

class AuthViewModel(
    private val repository: UserRepository
) : ViewModel() {

    var name: String? = null
    var email: String?= null
    var password: String? = null
    var passwordconfirm: String? = null

    var authListener: AuthListener? = null

    fun getLoggedInUser() = repository.getUser()

    fun onLoginButtonClick(view: View) {
        authListener?.onStarted()
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            authListener?.onFailure("Invalid email or password")
            return
        }

        Coroutines.main {
            try {
                val authResponse = repository.userLogin(email!!, password!!)
                authResponse.token?.let {
                    val jwt = JWT(it)
                    val id = jwt.getClaim("id")
                    val name = jwt.getClaim("name")
                    val u = User(id.asInt(), name.asString(), email, it)
                    authListener?.onSuccess(u)
                    repository.saveUser(u)
                    return@main
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!)
            } catch (e: ForbiddenException) {
                authListener?.onFailure(e.message!!)
            } catch (e: NotFoundException) {
                authListener?.onFailure(e.message!!)
            } finally {
                    authListener?.onFinish()
            }
        }
    }

    fun onLogin(view: View) {
        Intent(view.context, LoginActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun onSignup(view: View) {
        Intent(view.context, SignupActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun onSignupButtonClick(view: View) {
        authListener?.onStarted()

        if (name.isNullOrEmpty()) {
            authListener?.onFailure("Name is required")
            return
        }

        if (email.isNullOrEmpty()) {
            authListener?.onFailure("Email is required")
            return
        }

        if (password.isNullOrEmpty()) {
            authListener?.onFailure("Please enter a password")
            return
        }

        if (password != passwordconfirm) {
            authListener?.onFailure("Password dit not match")
            return
        }

        Coroutines.main {
            try {
                val authResponse = repository.userSignup(name!!, email!!, password!!)
                if (authResponse != null) {
                    authListener?.onSuccess(User())
                    return@main
                }
                authListener?.onFailure("problem_sign")
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!)
            } catch (e: ForbiddenException) {
                authListener?.onFinish()
            } catch (e: NotFoundException) {
                authListener?.onFailure(e.message!!)
            }
        }
    }
}
package com.esgi.picturization.ui.auth

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import com.esgi.picturization.data.db.entities.User
import com.esgi.picturization.data.repositories.UserRepository
import com.esgi.picturization.util.ApiException
import com.esgi.picturization.util.Coroutines
import com.esgi.picturization.util.NoInternetException

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
                    val u = User(3, "pierre", "pkettmus@gmail.com", it)
                    authListener?.onSuccess(u)
                    repository.saveUser(u)
                    return@main
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
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
                authResponse.token?.let {
                    val u = User(3, "pierre", "pkettmus@gmail.com", it)
                    authListener?.onSuccess(u)
                    repository.saveUser(u)
                    return@main
                }
                authListener?.onFailure("problem_sign")
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!)
            }
        }
    }
}
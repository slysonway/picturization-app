package com.esgi.picturization.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.auth0.android.jwt.JWT
import com.esgi.picturization.R
import com.esgi.picturization.data.db.entities.User
import com.esgi.picturization.databinding.ActivityLoginBinding
import com.esgi.picturization.ui.home.HomeActivity
import com.esgi.picturization.util.hide
import com.esgi.picturization.util.show
import com.esgi.picturization.util.snackbar
import kotlinx.android.synthetic.main.activity_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.time.LocalDate
import java.util.*

class LoginActivity : AppCompatActivity(), AuthListener, KodeinAware {

    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance<AuthViewModelFactory>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)
        binding.viewmodel = viewModel

        viewModel.authListener = this

        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                user.token?.let {
                    val expired = Date() > JWT(it).expiresAt
                    if (!expired) {
                        Intent(this, HomeActivity::class.java).also {
                            // use for when user use back key he doesn't come back to login activity
                            it.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(it)
                        }
                    }
                }

            }
        })
    }

    override fun onStarted() {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            getCurrentFocus()?.getWindowToken(),
            InputMethodManager.RESULT_UNCHANGED_SHOWN
        )
        btn_login.isEnabled = false
        progress_bar.show()
    }

    override fun onSuccess(user: User) {
        progress_bar.hide()
        btn_login.isEnabled = true
    }

    override fun onFailure(message: String) {
        progress_bar.hide()
        root_layout.snackbar(message)
        btn_login.isEnabled = true
    }

    override fun onFinish() {
        progress_bar.hide()
    }
}

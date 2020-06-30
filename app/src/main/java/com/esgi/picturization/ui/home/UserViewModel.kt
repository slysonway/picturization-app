package com.esgi.picturization.ui.home

import androidx.lifecycle.ViewModel
import com.esgi.picturization.data.repositories.UserRepository

class UserViewModel(val repository: UserRepository): ViewModel() {
    var user = repository.getUser()
}
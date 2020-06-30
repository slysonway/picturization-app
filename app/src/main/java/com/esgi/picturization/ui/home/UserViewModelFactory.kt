package com.esgi.picturization.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.esgi.picturization.data.repositories.UserRepository

class UserViewModelFactory(val repository: UserRepository):
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserViewModel(repository) as T
    }
}
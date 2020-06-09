package com.esgi.picturization.ui.home.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.esgi.picturization.data.repositories.ImageRepository
import com.esgi.picturization.data.repositories.UserRepository

@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory (
    private val repository: UserRepository,
    private val imageRepository: ImageRepository
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(repository, imageRepository) as T
    }
}
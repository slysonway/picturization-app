package com.esgi.picturization.ui.home.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.esgi.picturization.data.repositories.ImageRepository

@Suppress("UNCHECKED_CAST")
class StartViewModelFactory(
    private val imageRepository: ImageRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return StartViewModel(imageRepository) as T
    }
}
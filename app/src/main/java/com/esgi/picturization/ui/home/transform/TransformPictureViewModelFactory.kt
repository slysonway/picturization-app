package com.esgi.picturization.ui.home.transform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.esgi.picturization.data.repositories.ImageRepository

@Suppress("UNCHECKED_CAST")
class TransformPictureViewModelFactory(
    val repository: ImageRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TransformPictureViewModel(repository) as T
    }
}
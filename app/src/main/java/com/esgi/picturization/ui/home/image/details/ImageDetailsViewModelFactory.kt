package com.esgi.picturization.ui.home.image.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.esgi.picturization.data.repositories.ImageRepository

@Suppress("UNCHECKED_CAST")
class ImageDetailsViewModelFactory(
    private val imageRepository: ImageRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ImageDetailsViewModel(imageRepository) as T
    }
}
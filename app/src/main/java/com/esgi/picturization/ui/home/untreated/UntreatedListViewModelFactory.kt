package com.esgi.picturization.ui.home.untreated

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.esgi.picturization.data.repositories.ImageRepository

@Suppress("UNCHECKED_CAST")
class UntreatedListViewModelFactory(
    private val imageRepository: ImageRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UntreatedListViewModel(imageRepository) as T
    }
}
package com.esgi.picturization.ui.home.treated

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.esgi.picturization.data.repositories.ImageRepository

@Suppress("UNCHECKED_CAST")
class TreatedListViewModelFactory(
    private val imageRepository: ImageRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TreatedListViewModel(imageRepository) as T
    }
}
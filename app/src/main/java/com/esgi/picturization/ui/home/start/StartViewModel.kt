package com.esgi.picturization.ui.home.start

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esgi.picturization.data.models.DbImage
import com.esgi.picturization.data.models.Image
import com.esgi.picturization.data.repositories.ImageRepository
import com.esgi.picturization.util.ApiException
import com.esgi.picturization.util.Coroutines
import com.esgi.picturization.util.NoInternetException

class StartViewModel(
    private val imageRepository: ImageRepository
) : ViewModel() {
    val imageList: MutableLiveData<List<DbImage>> = MutableLiveData()

    var startListener: StartListener? = null

    fun getImage() {
        Coroutines.main {
            startListener?.onStarted()
            try {
                val images = imageRepository.getTreatedImage()
                //val images = imageRepository.getTreatedImage()
                imageList.value = images.sortedByDescending { it.createdAt }
                startListener?.onSuccess()
            } catch (e: ApiException) {
                e.printStackTrace()
                //transformListener?.onError()
            }catch (e: NoInternetException) {
                e.printStackTrace()
            } finally {
                startListener?.onFinish()
            }
        }
    }
}
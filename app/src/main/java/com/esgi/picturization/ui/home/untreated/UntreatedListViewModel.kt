package com.esgi.picturization.ui.home.untreated

import androidx.lifecycle.ViewModel
import com.esgi.picturization.data.models.DbImage
import com.esgi.picturization.data.repositories.ImageRepository
import com.esgi.picturization.util.ApiException
import com.esgi.picturization.util.Coroutines
import com.esgi.picturization.util.ForbiddenException
import com.esgi.picturization.util.NoInternetException

class UntreatedListViewModel(
    private val imageRepository: ImageRepository
) : ViewModel() {
    var imageList: List<DbImage> = ArrayList()
    var untreatedListener: UntreatedListener? = null

    fun getImage() {
        Coroutines.main {
            untreatedListener?.onStarted()
            try {
                val images = imageRepository.getUntreatedImage()
                imageList = images.sortedByDescending { it.createdAt }
                untreatedListener?.onSuccess()
            } catch (e: ApiException) {
                untreatedListener?.onError(e.message!!)
            } catch (e: NoInternetException) {
                untreatedListener?.onError(e.message!!)
            } catch (e: ForbiddenException) {
                untreatedListener?.onError(e.message!!)
            } finally {
                untreatedListener?.onFinish()
            }
        }
    }
}
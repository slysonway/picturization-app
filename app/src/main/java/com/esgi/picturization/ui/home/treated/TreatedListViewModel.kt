package com.esgi.picturization.ui.home.treated

import androidx.lifecycle.ViewModel
import com.esgi.picturization.data.models.DbImage
import com.esgi.picturization.data.repositories.ImageRepository
import com.esgi.picturization.util.ApiException
import com.esgi.picturization.util.Coroutines
import com.esgi.picturization.util.ForbiddenException
import com.esgi.picturization.util.NoInternetException

class TreatedListViewModel(
    private val imageRepository: ImageRepository
) : ViewModel() {
    var imageList: List<DbImage> = ArrayList()
    var treatedListener: TreatedListener? = null

    fun getImage() {
        Coroutines.main {
            treatedListener?.onStarted()
            try {
                val images = imageRepository.getTreatedImage()
                imageList = images.sortedByDescending { it.createdAt }
                treatedListener?.onSuccess()
            } catch (e: ApiException) {
                treatedListener?.onError(e.message!!)
            } catch (e: NoInternetException) {
                treatedListener?.onError(e.message!!)
            } catch (e: ForbiddenException) {
                treatedListener?.onError(e.message!!)
            } finally {
                treatedListener?.onFinish()
            }
        }
    }
}
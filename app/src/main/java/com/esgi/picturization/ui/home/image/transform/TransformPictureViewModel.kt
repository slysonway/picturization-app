package com.esgi.picturization.ui.home.image.transform

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esgi.picturization.data.models.FilterEnum
import com.esgi.picturization.data.models.Image
import com.esgi.picturization.data.repositories.ImageRepository
import com.esgi.picturization.util.ApiException
import com.esgi.picturization.util.Coroutines
import com.esgi.picturization.util.NoInternetException

class TransformPictureViewModel(
    private val repository: ImageRepository
): ViewModel() {
    val image: MutableLiveData<Image> = MutableLiveData()
    val filterList: ArrayList<FilterEnum> = ArrayList()
    var transformListener: TransformListener? = null

    fun getImageUri(): Uri {
        return image.value!!.file.toUri()
    }

    fun sendImage(v: View) {
        Coroutines.main {
            transformListener?.onStarted()
            try {
                val toSend = Image(image.value!!.file, filterList)

                val t = repository.sendImage(toSend)
                Log.d(this::class.java.simpleName, t.toString())

            } catch (e: ApiException) {
                e.printStackTrace()
                //transformListener?.onError()
            }catch (e: NoInternetException) {
                e.printStackTrace()
            } finally {
                transformListener?.onFinish()
            }
        }
    }
}
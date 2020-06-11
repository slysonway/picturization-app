package com.esgi.picturization.ui.home.transform

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TransformPictureViewModel: ViewModel() {
    val imagePreview: MutableLiveData<Uri> = MutableLiveData()
}
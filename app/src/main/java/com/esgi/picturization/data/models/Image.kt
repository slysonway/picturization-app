package com.esgi.picturization.data.models

import android.net.Uri
import java.io.File
import java.io.Serializable

data class Image(
    val file: File,
    val filters: List<FilterEnum>
) : Serializable
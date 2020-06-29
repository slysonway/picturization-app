package com.esgi.picturization.data.models

import java.io.File
import java.io.Serializable

data class Image(
    val file: File,
    val filters: List<Filter>
) : Serializable
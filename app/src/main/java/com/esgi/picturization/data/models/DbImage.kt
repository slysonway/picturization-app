package com.esgi.picturization.data.models

import java.util.*

data class DbImage(
    val id: Int,
    val url: String,
    val filters: List<List<String>>,
    val createdAt: Date,
    val updatedAt: Date,
    val treaty: Boolean
)
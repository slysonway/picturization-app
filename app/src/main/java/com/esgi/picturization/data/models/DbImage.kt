package com.esgi.picturization.data.models

import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

data class DbImage(
    val id: Int,
    val urlUntreated: String,
    val urlTreated: String,
    val filters: HashMap<String, String>,
    val createdAt: Date,
    val updatedAt: Date?,
    val treaty: Boolean
) : Serializable
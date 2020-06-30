package com.esgi.picturization.data.models

import java.io.Serializable

data class Filter (
    val name: FilterEnum,
    val parameter: List<FilterParameter>
) : Serializable
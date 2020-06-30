package com.esgi.picturization.data.models

import java.io.Serializable

data class FilterParameter(
    val name: FilterParameterEnum,
    var value: String
) : Serializable
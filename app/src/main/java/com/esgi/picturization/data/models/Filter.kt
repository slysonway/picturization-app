package com.esgi.picturization.data.models

data class Filter (
    val name: FilterEnum,
    val parameter: List<FilterParameter>
)
package com.esgi.picturization.data.models

import com.esgi.picturization.R

enum class FilterParameterEnum(val title: Int) {
    intensity(R.string.intensity),
    orientation(R.string.orientation),
    size(R.string.size),
    color_filter_1(R.string.color_filter_1),
    color_filter_2(R.string.color_filter_2),
    colored_chars(R.string.colored_chars),
    quality_reduction(R.string.quality_reduction)
}
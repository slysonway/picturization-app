package com.esgi.picturization.data.models

import com.esgi.picturization.R

enum class FilterEnum(val title: Int, val icon: Int) {
    GAUSSIAN_BLUR(R.string.gaussian_blur, R.drawable.ic_gaussian_blur_black_24dp),
    RADIAL_BLUR(R.string.radial_blur, R.drawable.ic_blur_radial_black_24dp),
    COLOR_INVERSION(R.string.color_inversion, R.drawable.ic_invert_colors_black_24dp),
    ZOOM(R.string.zoom, R.drawable.ic_zoom_in_black_24dp),
    COMPRESSION(R.string.compression, R.drawable.ic_photo_compress_black_24dp),
    GRAYSCALE(R.string.grayscale, R.drawable.ic_gray_scale_black_24dp),
    BICHROMATIC(R.string.bichromatic, R.drawable.ic_bichromatic_black_24dp),
    ASCII_IMAGE_CONVERSION(R.string.ascii_image_conversion, R.drawable.ic_ascii_conversion_black_24dp)
}
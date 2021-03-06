package com.esgi.picturization.util

import java.io.IOException

class ApiException(message: String): IOException(message)
class NoInternetException(message: String): IOException(message)
class UnauthorizedException(message:String): IOException(message)
class ForbiddenException(message:String): IOException(message)
class NotFoundException(message: String): IOException(message)
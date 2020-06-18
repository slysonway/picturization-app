package com.esgi.picturization.data.network

import android.content.Intent
import com.esgi.picturization.PicturizationApplication
import com.esgi.picturization.ui.auth.LoginActivity
import com.esgi.picturization.util.ApiException
import com.esgi.picturization.util.ForbiddenException
import com.esgi.picturization.util.NotFoundException
import com.esgi.picturization.util.UnauthorizedException
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.lang.Exception
import java.lang.StringBuilder

abstract class SafeApiRequest {

    suspend fun<T: Any> apiRequest(call: suspend () -> Response<T>): T {
        val response = call.invoke()

        if (response.isSuccessful) {
            return response.body()!!
        } else {
            val error = response.errorBody()?.string()
            var message = "unknow"
            error?.let {
                try {
                    if (JSONObject(it).has("error")) {
                        message = JSONObject(it).getString("error")
                    } else {
                        if (JSONObject(it).has("error_description")) {
                            message = JSONObject(it).getString("error_description")
                        }
                    }
                } catch (e: Exception) {
                    throw ApiException("error_internal_server")
                }
            }
            when (response.code()) {
                500 -> throw ApiException(message)
                401 -> {
                    Intent(PicturizationApplication.context, LoginActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        it.putExtra("EXCEPTION_CODE", message)
                        PicturizationApplication.context!!.startActivity(it)
                    }
                    throw ApiException(message)
                }
                403 -> {
                    Intent(PicturizationApplication.context, LoginActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        it.putExtra("EXCEPTION_CODE", message)
                        PicturizationApplication.context!!.startActivity(it)
                    }
                    throw ForbiddenException(message)
                }
                404 -> throw NotFoundException(message)
                else -> throw ApiException(message)

            }
        }
    }
}
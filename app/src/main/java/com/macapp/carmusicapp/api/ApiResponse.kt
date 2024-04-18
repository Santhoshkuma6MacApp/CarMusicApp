package com.macapp.carmusicapp.api

sealed class Response<T>(
    val data: T? = null,
    val errorMessage: String? = null,
    val showLoader: Boolean? = null
)
{
    class Success<T>(data: T? = null) : Response<T>(data = data)
    class Error<T>(errorMessage: String) : Response<T>(errorMessage = errorMessage)
    class Loading<T>(showLoader: Boolean) : Response<T>(showLoader = showLoader)

}
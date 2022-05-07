package com.joao.zipcodeapp.util

sealed class Resource<T> (
    val data: T? = null,
    val exception: CustomExceptions? = null
) {
    class Success<T>(data: T?): Resource<T>(data)

    class Error<T>(data: T? = null, exception: CustomExceptions?): Resource<T>(data, exception)
}
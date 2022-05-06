package com.joao.zipcodeapp.util

sealed class CustomExceptions{

    object UnableToReadFile : CustomExceptions()

    object UnknownException : CustomExceptions()
}

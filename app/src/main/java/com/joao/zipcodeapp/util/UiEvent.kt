package com.joao.zipcodeapp.util

sealed class UiEvent(){

    object Loading : UiEvent()
    object ZipCodesLoaded: UiEvent()
    object Failed : UiEvent()
    object NoInternetConnection : UiEvent()
}
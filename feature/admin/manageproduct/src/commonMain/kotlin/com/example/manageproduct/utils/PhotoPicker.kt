package com.example.manageproduct.utils

import androidx.compose.runtime.Composable

import io.ktor.http.content.PartData


expect class PhotoPicker {


    fun open()

    @Composable
    fun initializePhotoPicker(onImageSelect:(ByteArray?)->Unit)

}
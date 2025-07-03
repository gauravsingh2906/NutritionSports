package com.example.manageproduct.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.io.File
//
//actual class PhotoPicker {
//
//    private var openPhotoPicker = mutableStateOf(false)
//
//    @Composable
//    actual fun initializePhotoPicker(
//        onImageSelect: (ByteArray?) -> Unit
//    ) {
//        val context = LocalContext.current
//       val openPhotoPickerState = remember { openPhotoPicker }
//        val pickMedia = rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.PickVisualMedia()
//        )  { uri->
//            if(uri!=null) {
//                val bytes = uriToByteArray(context,uri)
//                onImageSelect(bytes)
//                openPhotoPicker.value =false
//            } else {
//                onImageSelect(null)
//                openPhotoPicker.value = false
//            }
//        }
//
//        LaunchedEffect(openPhotoPickerState) {
//            if (openPhotoPickerState.value) {
//                pickMedia.launch(
//                    PickVisualMediaRequest(
//                        ActivityResultContracts.PickVisualMedia.ImageOnly
//                    )
//                )
//            }
//        }
//
//    }
//
//    actual fun open() {
//         openPhotoPicker.value = true
//    }
//
//}
//
//private fun uriToByteArray(context:Context,uri:Uri):ByteArray {
//    return context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: byteArrayOf()
//}

actual class PhotoPicker {
    private var openPhotoPicker = mutableStateOf(false)

    actual fun open() {
        openPhotoPicker.value = true
    }

    @Composable
    actual fun initializePhotoPicker(
        onImageSelect: (ByteArray?) -> Unit,
    ) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val openPhotoPickerState by remember { openPhotoPicker }

        val pickMedia = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                coroutineScope.launch {
                    val byteArray = uriToByteArray(context, uri)
                    Log.e("Byte",byteArray.toString())
                    onImageSelect(byteArray)
                }
            } else {
                onImageSelect(null)
            }
            openPhotoPicker.value = false
        }

        LaunchedEffect(openPhotoPickerState) {
            if (openPhotoPickerState) {
                pickMedia.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        }
    }

    private suspend fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.readBytes()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

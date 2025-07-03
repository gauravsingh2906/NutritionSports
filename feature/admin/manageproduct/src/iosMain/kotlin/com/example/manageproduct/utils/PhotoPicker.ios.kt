package com.example.manageproduct.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import kotlinx.cinterop.*
import platform.CoreServices.kUTTypeImage
import platform.Foundation.NSData
import platform.UIKit.UIImageJPEGRepresentation
import platform.Foundation.NSString
import platform.UniformTypeIdentifiers.UTType
import platform.UIKit.*
import platform.darwin.NSObject
import platform.posix.memcpy


actual class PhotoPicker {

    private var onImageSelectedCallback: ((ByteArray?) -> Unit)? = null

    @OptIn(ExperimentalForeignApi::class)
    actual fun open() {
        val picker = UIImagePickerController().apply {
            sourceType =
                UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
            mediaTypes = listOf("public.image")
            delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol,
                UINavigationControllerDelegateProtocol {

                override fun imagePickerController(
                    picker: UIImagePickerController,
                    didFinishPickingMediaWithInfo: Map<Any?, *>
                ) {
                    val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage

                    val data = image?.let { UIImageJPEGRepresentation(it,0.8) }
                    val bytes = data?.toByteArray()
                    onImageSelectedCallback?.invoke(bytes)
                    picker.dismissViewControllerAnimated(true, completion = null)
                }

                override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                    onImageSelectedCallback?.invoke(null)
                    picker.dismissViewControllerAnimated(true, completion = null)
                }
            }
        }

        val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootVC?.presentViewController(picker, animated = true, completion = null)
    }

    @Composable
    actual fun initializePhotoPicker(onImageSelect: (ByteArray?) -> Unit) {
       DisposableEffect(Unit) {
           onImageSelectedCallback = onImageSelect
           onDispose {
               onImageSelectedCallback = null
           }
       }
    }
}

// Convert NSData to ByteArray using safe pointer copy
@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    val size = this.length.toInt()
    val byteArray = ByteArray(size)
    memScoped {
        val destination = byteArray.refTo(0).getPointer(this)
        memcpy(destination, this@toByteArray.bytes, size.convert())
    }
    return byteArray
}
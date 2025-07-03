package com.example.nutritionsports

import androidx.compose.ui.window.ComposeUIViewController
import com.example.di.initializeKoin

fun MainViewController() = ComposeUIViewController (
    configure = { initializeKoin() }
){
    App()
}
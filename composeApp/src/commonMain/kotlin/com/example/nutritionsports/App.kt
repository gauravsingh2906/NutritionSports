package com.example.nutritionsports

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.domain.CustomerRepository
import com.example.shared.navigation.Screen
import com.example.navigation.SetUpNavGraph
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import org.koin.compose.koinInject

@Composable
fun App() {
    MaterialTheme {

        val customerRepository = koinInject<CustomerRepository>()

        var appReady by remember { mutableStateOf(false) }

        val isUserAuthenticated = remember { customerRepository.getCurrentUserId() !=null }
        val startDestination = remember {
            if (isUserAuthenticated) Screen.HomeGraph
            else Screen.Auth
        }

        LaunchedEffect(Unit) {
            GoogleAuthProvider.create(credentials = GoogleAuthCredentials(serverId = "496441513720-f6g2h66egatj2up3r3r9lkbc593icd3l.apps.googleusercontent.com"))
            appReady=true
        }

     AnimatedVisibility(
         modifier = Modifier.fillMaxSize(),
         visible = appReady
     ) {
         SetUpNavGraph(
             startDestination = startDestination
         )
     }

    }
}
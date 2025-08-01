package com.example.auth

import ContentWithMessageBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.auth.component.GoogleButton
import com.example.shared.Alpha
import com.example.shared.BebasNeueFont
import com.example.shared.FontSize
import com.example.shared.RobotoCondensedFont
import com.example.shared.Surface
import com.example.shared.SurfaceBrand
import com.example.shared.SurfaceError
import com.example.shared.TextPrimary
import com.example.shared.TextSecondary
import com.example.shared.TextWhite
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@Composable
fun AuthScreen(
    navigateToHoneScreen: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val viewModel = koinViewModel<AuthViewModel>()
    val messageBarState = rememberMessageBarState()
    var loadingState by remember { mutableStateOf(false) }

    Scaffold { padding ->
        ContentWithMessageBar(
            contentBackgroundColor = Surface,
            modifier = Modifier
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                ),
            messageBarState = messageBarState,
            errorMaxLines = 2,
            errorContainerColor = SurfaceError,
            errorContentColor = TextWhite,
            successContainerColor = SurfaceBrand,
            successContentColor = TextPrimary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize().padding(24.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "NUTRISPORT",
                        textAlign = TextAlign.Center,
                        fontSize = FontSize.EXTRA_LARGE,
                        fontFamily = BebasNeueFont(),
                        color = TextSecondary
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth().alpha(Alpha.HALF),
                        text = "Sign in to continue",
                        textAlign = TextAlign.Center,
                        fontSize = FontSize.EXTRA_REGULAR,
                        fontFamily = RobotoCondensedFont(),
                        color = TextPrimary,
                    )
                }
                GoogleButtonUiContainerFirebase(
                    linkAccount = false,
                    onResult = { result ->
                        result.onSuccess { user ->
                            viewModel.createCustomer(
                                user = user,
                                onSuccess = {
                                    scope.launch {
                                        messageBarState.addSuccess("Authentication Successful!")
                                        delay(2000)
                                        navigateToHoneScreen()
                                    }
                                },
                                onError = { message ->
                                    messageBarState.addError(message)
                                }
                            )
                            messageBarState.addSuccess("Authentication Successfully")
                            loadingState = false


                        }.onFailure { error ->
                            if (error.message?.contains("A network error") == true) {
                                messageBarState.addError("Internet connection unavailable")
                            } else if (error.message?.contains("Idtoken is null") == true) {
                                messageBarState.addError("Sign in Canceled")
                            } else {
                                messageBarState.addError(error.message ?: "Unknown")
                            }
                            loadingState = false
                        }
                    }
                ) {
                    GoogleButton(
                        loading = loadingState,
                        onClick = {
                            loadingState = true
                            this@GoogleButtonUiContainerFirebase.onClick()
                        }
                    )
                }


            }

        }
    }

}




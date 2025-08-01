package com.example.auth.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.shared.BorderIdle
import com.example.shared.FontSize
import com.example.shared.IconSecondary
import com.example.shared.Resources
import com.example.shared.SurfaceDarker
import com.example.shared.SurfaceLighter
import com.example.shared.TextPrimary
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun GoogleButton(
    modifier: Modifier=Modifier,
    loading:Boolean=false,
    primaryText:String = "Sign in with google",
    secondaryText:String = "Please wait...",
    icon:DrawableResource = Resources.Image.GoogleLogo,
    shape: Shape = RoundedCornerShape(size=99.dp),
    backgroundColor:Color = SurfaceLighter,
    borderColor: Color = BorderIdle,
    progressIndicatorColor:Color = IconSecondary,
    onClick:()->Unit
) {

    var buttonText by remember { mutableStateOf(primaryText) }

    LaunchedEffect(loading) {
        buttonText = if (loading) secondaryText else primaryText
    }

    Surface(
        modifier = modifier
            .clip(shape)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(enabled = !loading) {
                onClick()
            },
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 20.dp)
                .animateContentSize(
                    animationSpec = tween(durationMillis = 200)
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = !loading
            ) {
                Icon(painter = painterResource(icon), tint = Color.Unspecified, contentDescription = "Google")
            }


            AnimatedVisibility(
                visible = loading
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = progressIndicatorColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            Text(text = buttonText, fontSize = FontSize.REGULAR, color = TextPrimary)
        }
    }








}













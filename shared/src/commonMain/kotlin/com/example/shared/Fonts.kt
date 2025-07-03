package com.example.shared

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import nutritionsports.shared.generated.resources.Res
import nutritionsports.shared.generated.resources.bebas_neue_regular
import nutritionsports.shared.generated.resources.roboto_condensed_medium

@Composable
fun BebasNeueFont() = FontFamily(
    Font(Res.font.bebas_neue_regular)
)

@Composable
fun RobotoCondensedFont() = FontFamily(
    Font(Res.font.roboto_condensed_medium)
)

object FontSize {

    val EXTRA_SMALL = 10.sp
    val SMALL = 12.sp
    val REGULAR = 14.sp
    val EXTRA_REGULAR=16.sp
    val MEDIUM = 18.sp
    val EXTRA_MEDIUM = 20.sp
    val LARGE = 30.sp
    val EXTRA_LARGE = 40.sp

}


package com.example.shared.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shared.FontSize
import com.example.shared.IconPrimary
import com.example.shared.Resources
import com.example.shared.SurfaceBrand
import com.example.shared.SurfaceLighter
import com.example.shared.TextPrimary
import com.example.shared.domain.QuantityCounterSize
import nutritionsports.shared.generated.resources.Res
import org.jetbrains.compose.resources.painterResource

@Composable
fun QuantityCounter(
    modifier: Modifier = Modifier,
    size: QuantityCounterSize,
    value: Int,
    onMinusClick: (Int) -> Unit,
    onPLusClick: (Int) -> Unit
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(size.spacing)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SurfaceBrand)
                .clickable { if (value>1) onMinusClick(value-1)}
                .padding(size.padding)
        ) {
            Icon(
                modifier = Modifier.size(14.dp),
                painter = painterResource(Resources.Icon.Minus),
                contentDescription = "Minus Icon",
                tint = IconPrimary
            )
        }
        Box(
            modifier = Modifier
                .background(SurfaceLighter)
                .clip(RoundedCornerShape(6.dp))
                .padding(size.padding)
        ) {
            Text(
                text = "+$value",
                fontSize = FontSize.SMALL,
                lineHeight = FontSize.SMALL*1,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SurfaceBrand)
                .clickable {
                    if (value<10) onPLusClick(value+1)
                }
                .padding(size.padding)
        ) {
            Icon(
                modifier = Modifier.size(14.dp),
                painter = painterResource(Resources.Icon.plus),
                contentDescription = "Plus Icon",
                tint = IconPrimary
            )
        }
    }


}
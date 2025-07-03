package com.example.home.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.home.domain.BottomBarDestination
import com.example.shared.IconPrimary
import com.example.shared.IconSecondary
import com.example.shared.SurfaceLighter
import com.example.shared.domain.Customer
import com.example.shared.utils.RequestState
import org.jetbrains.compose.resources.painterResource

@Composable
fun BottomBarNavigation(
    modifier: Modifier = Modifier,
    customer:RequestState<Customer>,
    selected: BottomBarDestination,
    onSelect: (BottomBarDestination) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceLighter)
            .padding(
                vertical = 24.dp, horizontal = 36.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BottomBarDestination.entries.forEach { destinaion ->
            val animatedTint by animateColorAsState(
                targetValue = if (selected==destinaion) IconSecondary else IconPrimary
            )

           Box(
               contentAlignment = Alignment.TopEnd
           ){
               Icon(
                   modifier = Modifier.clickable {
                       onSelect(destinaion)
                   },
                   painter = painterResource(destinaion.icon),
                   tint = animatedTint,
                   contentDescription = destinaion.title
               )
               if(destinaion == BottomBarDestination.Cart) {
                   AnimatedContent(
                       targetState = customer
                   ) { customerState->
                       if(customerState.isSuccess() && customerState.getSuccessData().cart.isNotEmpty()) {
                           Box(
                               modifier = Modifier
                                   .size(8.dp)
                                   .offset(x=4.dp,y= (-5).dp)
                                   .clip(CircleShape).background(IconSecondary)
                           )
                       }
                   }
               }
           }

        }


    }
}
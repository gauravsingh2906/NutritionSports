package com.example.productsoverview

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.productsoverview.component.MainProductCard
import com.example.shared.Alpha
import com.example.shared.FontSize
import com.example.shared.Resources
import com.example.shared.TextPrimary
import com.example.shared.component.InfoCard
import com.example.shared.component.LoadingCard
import com.example.shared.component.ProductCard
import com.example.shared.domain.Product
import com.example.shared.utils.DisplayResult
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductsOverviewScreen(
    navigateToDetailScreen:(String)->Unit
) {

    val viewModel = koinViewModel<ProductsOverviewViewModel>()

    val products by viewModel.products.collectAsState()

    val listState = rememberLazyListState()

    val centeredIndex: Int? by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                val itemCenter = item.offset + item.size / 2
                kotlin.math.abs(itemCenter - viewportCenter)
            }?.index
        }
    }



    products.DisplayResult(
        onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
        onSuccess = { productList ->
            AnimatedContent(
                targetState = productList.distinctBy { it.id }
            ) { products ->
                if (products.isNotEmpty()) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            state = listState,
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            itemsIndexed(
                                items = products
                                    .filter { it.isNew }
                                    .sortedBy{it.createdAt},
                                key = { index, item -> item.id }
                            ) { index, product ->
                                val isCentered = index == centeredIndex

                                val animatedScale by animateFloatAsState(
                                    targetValue = if (isCentered) 1f else 0.8f,
                                    animationSpec = tween(300)
                                )

                                MainProductCard(
                                    modifier = Modifier
                                        .scale(animatedScale)
                                        .height(300.dp)
                                        .fillParentMaxWidth(0.6f),
                                    product = product,
                                    isLarge = isCentered,
                                    onClick = {navigateToDetailScreen(it)}
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            modifier = Modifier.fillMaxWidth().alpha(Alpha.HALF),
                            textAlign = TextAlign.Center,
                            text = "Discounted products",
                            fontSize = FontSize.EXTRA_REGULAR,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyColumn(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = products
                                    .filter { it.isDiscounted }
                                    .sortedBy { it.createdAt }
                                    .take(3),
                                key = { it.id }
                            ) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = {navigateToDetailScreen(it)}
                                )
                            }
                        }
                    }

                } else {
                    InfoCard(
                        image = Resources.Image.Cat,
                        title = "Nothing here",
                        subtitle = "Empty product list."
                    )
                }
            }
        },
        onError = { message ->
            InfoCard(
                image = Resources.Image.Cat,
                title = "Oops!",
                subtitle = message
            )
        }
    )
}







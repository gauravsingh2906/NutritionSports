package com.example.categorysearch

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shared.BebasNeueFont
import com.example.shared.BorderIdle
import com.example.shared.FontSize
import com.example.shared.IconPrimary
import com.example.shared.Resources
import com.example.shared.Surface
import com.example.shared.SurfaceLighter
import com.example.shared.TextPrimary
import com.example.shared.component.InfoCard
import com.example.shared.component.LoadingCard
import com.example.shared.component.ProductCard
import com.example.shared.domain.ProductCategory
import com.example.shared.utils.DisplayResult
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySearchScreen(
    navigateBack:()->Unit,
    navigateToDetails:(String)->Unit,
    category:ProductCategory
) {

    val viewModel = koinViewModel<CategoriesSearchViewModel>()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var searchBarVisible by mutableStateOf(false)

   Scaffold (
       containerColor = Surface,
       topBar = {
           AnimatedContent(
               targetState = searchBarVisible
           ) { visible->

               if(visible) {
                   SearchBar(
                       modifier = Modifier
                           .padding(horizontal = 12.dp)
                           .fillMaxWidth(),
                       inputField = {
                           SearchBarDefaults.InputField(
                               modifier = Modifier.fillMaxWidth(),
                               query = searchQuery,
                               onQueryChange = viewModel::updateSearchQuery,
                               expanded = false,
                               onExpandedChange = {},
                               onSearch = {},
                               placeholder = {
                                   Text(
                                       text = "Search here",
                                       fontSize = FontSize.REGULAR,
                                       color = TextPrimary
                                   )
                               },
                               trailingIcon = {
                                   IconButton(
                                       modifier = Modifier.size(14.dp),
                                       onClick = {
                                           if (searchQuery.isNotEmpty()) {
                                               viewModel.updateSearchQuery("")
                                           } else {
                                               searchBarVisible =false
                                           }

                                       }
                                   ) {
                                       Icon(
                                           painter = painterResource(Resources.Icon.Close),
                                           contentDescription = "Close Icon",
                                           tint = IconPrimary
                                       )
                                   }
                               },
                           )
                       },
                       expanded = false,
                       onExpandedChange = {},
                       colors = SearchBarColors(
                           containerColor = SurfaceLighter,
                           dividerColor = BorderIdle
                       ),
                       content = {}
                   )
               } else {
                   TopAppBar(
                       title = {
                           Text(
                               text = category.title,
                               fontFamily = BebasNeueFont(),
                               fontSize = FontSize.LARGE,
                               color = TextPrimary
                           )
                       },
                       navigationIcon = {
                           IconButton(
                               onClick = navigateBack
                           ) {
                               Icon(
                                   painter = painterResource(Resources.Icon.BackArrow),
                                   contentDescription = "Back Arrow Icon",
                                   tint = IconPrimary
                               )
                           }
                       },
                       actions = {
                           IconButton(
                               onClick = {
                                   searchBarVisible = true
                               }
                           ) {
                               Icon(
                                   painter = painterResource(Resources.Icon.Search),
                                   contentDescription = "Search Icon",
                                   tint = IconPrimary
                               )
                           }
                       },
                       colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                           containerColor = Surface,
                           scrolledContainerColor = Surface,
                           navigationIconContentColor = IconPrimary,
                           titleContentColor = TextPrimary,
                           actionIconContentColor = IconPrimary
                       )
                   )
               }
           }
       }
   ){ padding->
       filteredProducts.DisplayResult(
           modifier = Modifier
               .padding(
                   top = padding.calculateTopPadding(),
                   bottom = padding.calculateBottomPadding()
               ),
           onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
           onSuccess = { categoryProduct->
              AnimatedContent(
                  targetState = categoryProduct
              ) { products->
                  if (products.isNotEmpty()) {
                      LazyColumn(
                          modifier = Modifier.fillMaxSize().padding(all = 12.dp),
                          verticalArrangement = Arrangement.spacedBy(12.dp)
                      ){
                          items(
                              items = products,
                              key = {it.id}
                          ) { product->
                              ProductCard(
                                  product=product,
                                  onClick = navigateToDetails
                              )
                          }
                      }
                  } else{
                      InfoCard(
                          image = Resources.Image.Cat,
                          title = "Nothing here",
                          subtitle = "We couldn't find any product."
                      )
                  }
              }
           },
           onError = {
               InfoCard(
                   image = Resources.Image.Cat,
                   title = "Oops!",
                   subtitle = it
               )
           },
           transitionSpec = fadeIn() togetherWith fadeOut()

       )
   }

}
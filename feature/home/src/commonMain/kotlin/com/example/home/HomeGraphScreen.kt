package com.example.home

import ContentWithMessageBar
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.cart.CartScreen
import com.example.categories.CategoryScreen
import com.example.categorysearch.CategorySearchScreen
import com.example.home.component.BottomBarNavigation
import com.example.home.component.CustomDrawer
import com.example.home.domain.BottomBarDestination
import com.example.home.domain.CustomDrawerState
import com.example.home.domain.isOpened
import com.example.home.domain.opposite
import com.example.productsoverview.ProductsOverviewScreen
import com.example.shared.Alpha
import com.example.shared.BebasNeueFont
import com.example.shared.FontSize
import com.example.shared.IconPrimary
import com.example.shared.Resources
import com.example.shared.Surface
import com.example.shared.SurfaceLighter
import com.example.shared.TextPrimary
import com.example.shared.domain.ProductCategory
import com.example.shared.navigation.Screen
import com.example.shared.utils.RequestState
import com.example.shared.utils.getScreenWidth
import nutritionsports.feature.home.generated.resources.Res

import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeGraphScreen(
    navigateToAuth: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToAdmin: () -> Unit,
    navigateToDetails: (String) -> Unit,
    navigateToCategorySearch: (String) -> Unit,
    navigateToCheckOut: (String) -> Unit
) {

    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState()
    //  var selectedDestination by remember { mutableStateOf(BottomBarDestination.ProductsOverview) }

    val selectedDestination by remember {
        derivedStateOf {
            val route = currentRoute.value?.destination?.route.toString()
            when {
                route.contains(BottomBarDestination.ProductsOverview.screen.toString()) -> BottomBarDestination.ProductsOverview
                route.contains(BottomBarDestination.Cart.screen.toString()) -> BottomBarDestination.Cart
                route.contains(BottomBarDestination.Categories.screen.toString()) -> BottomBarDestination.Categories
                else -> BottomBarDestination.ProductsOverview
            }
        }
    }

    val screenWidth = remember { getScreenWidth() }
    var drawerState by remember { mutableStateOf(CustomDrawerState.Closed) }

    val offsetValue by remember { derivedStateOf { (screenWidth / 1.5).dp } }
    val animatedOffSet by animateDpAsState(
        targetValue = if (drawerState.isOpened()) offsetValue else 0.dp
    )

    val animatedBackground by animateColorAsState(
        targetValue = if (drawerState.isOpened()) SurfaceLighter else Surface
    )

    val animateScale by animateFloatAsState(
        targetValue = if (drawerState.isOpened()) 0.9f else 1f
    )

    val animateRadius by animateDpAsState(
        targetValue = if (drawerState.isOpened()) 20.dp else 0.dp
    )

    val viewModel = koinViewModel<HomeGraphViewModel>()
    val customer by viewModel.customer.collectAsState()
    val messageBarState = rememberMessageBarState()
    val totalAmount by viewModel.totalAmountFlow.collectAsState(RequestState.Loading)

    LaunchedEffect(totalAmount) {
        println("Total Amount: $totalAmount")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBackground)
            .systemBarsPadding()
    ) {

        CustomDrawer(
            customer = customer,
            onProfileClick = navigateToProfile,
            onContactUsLink = {},
            onSignOutClick = {
                viewModel.signOut(
                    onSuccess = navigateToAuth,
                    onError = { message ->
                        messageBarState.addError(message)
                    }
                )
            },
            onAdminPanelClick = navigateToAdmin
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(animateRadius))
                .offset(x = animatedOffSet)
                .scale(scale = animateScale)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(size = animateRadius),
                    ambientColor = Color.Black.copy(alpha = Alpha.DISABLED),
                    spotColor = Color.Black.copy(alpha = Alpha.DISABLED)
                )
        ) {
            Scaffold(
                containerColor = Surface,
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            AnimatedContent(
                                targetState = selectedDestination
                            ) { destination ->
                                Text(
                                    text = destination.title,
                                    fontFamily = BebasNeueFont(),
                                    fontSize = FontSize.LARGE,
                                    color = TextPrimary
                                )
                            }

                        },
                        actions = {
                            AnimatedVisibility(
                                visible = selectedDestination == BottomBarDestination.Cart
                            ) {
                               if (customer.isSuccess() && customer.getSuccessData().cart.isNotEmpty()) {
                                   IconButton(
                                       onClick = {
                                           if (totalAmount.isSuccess()) {
                                               navigateToCheckOut(
                                                   totalAmount.getSuccessData().toString()
                                               )
                                           } else if (totalAmount.isError()) {
                                               messageBarState.addError("Error while calculating a total amount: ${totalAmount.getErrorMessage()} ")
                                           }
                                       }
                                   ) {
                                       Icon(
                                           painter = painterResource(Resources.Icon.RightArrow),
                                           contentDescription = "Right Arrow",
                                           tint = IconPrimary
                                       )
                                   }
                               }

                            }
                        },
                        navigationIcon = {
                            AnimatedContent(
                                targetState = drawerState
                            ) { drawer ->
                                if (drawer.isOpened()) {
                                    IconButton(
                                        onClick = {
                                            drawerState = drawerState.opposite()
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(Resources.Icon.Close),
                                            contentDescription = "Close Icon",
                                            tint = IconPrimary
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            drawerState = drawerState.opposite()
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(Resources.Icon.Menu),
                                            contentDescription = "Menu Icon",
                                            tint = IconPrimary
                                        )
                                    }
                                }

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
            ) { paddingValues ->
                ContentWithMessageBar(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding()
                        ),
                    messageBarState = messageBarState,
                    errorMaxLines = 2,
                    contentBackgroundColor = Surface
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        NavHost(
                            modifier = Modifier.weight(1f),
                            navController = navController,
                            startDestination = Screen.ProductsOverview
                        ) {
                            composable<Screen.ProductsOverview> {
                                ProductsOverviewScreen(
                                    navigateToDetailScreen = navigateToDetails
                                )
                            }
                            composable<Screen.Cart> {
                                CartScreen()
                            }
                            composable<Screen.Categories> {
                                CategoryScreen(
                                    navigateToCategoriesSearch = navigateToCategorySearch
                                )
                            }


                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .padding(all = 12.dp)
                        ) {
                            BottomBarNavigation(
                                selected = selectedDestination,
                                onSelect = { destination ->
                                    navController.navigate(destination.screen) {
                                        launchSingleTop = true
                                        popUpTo<Screen.ProductsOverview> {
                                            saveState = true
                                            inclusive = false
                                        }
                                        restoreState = true
                                    }
                                },
                                customer = customer
                            )
                        }

                    }
                }

            }
        }


    }


}
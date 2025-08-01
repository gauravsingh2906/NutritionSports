package com.example.home.domain

import com.example.shared.Resources
import com.example.shared.navigation.Screen
import org.jetbrains.compose.resources.DrawableResource

enum class BottomBarDestination(
    val icon:DrawableResource,
    val title:String,
    val screen:Screen
) {

    ProductsOverview(
        icon = Resources.Icon.Home,
        title = "Nutri Sport",
        screen = Screen.ProductsOverview
    ),
    Cart(
        icon = Resources.Icon.ShoppingCart,
        title = "Cart",
        screen = Screen.Cart
    ),
    Categories(
        icon = Resources.Icon.Categories,
        title = "Categories",
        screen = Screen.Categories
    )

}
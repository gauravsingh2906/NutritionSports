package com.example.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.admin.AdminScreen
import com.example.auth.AuthScreen
import com.example.categorysearch.CategorySearchScreen
import com.example.checkout.CheckoutScreen
import com.example.details.DetailsScreen
import com.example.home.HomeGraphScreen
import com.example.manageproduct.ManageProductScreen
import com.example.paymentcompleted.PaymentCompleted
import com.example.profile.ProfileScreen
import com.example.shared.domain.ProductCategory
import com.example.shared.navigation.Screen

@Composable
fun SetUpNavGraph(
   startDestination: Screen = Screen.Auth
){

   val navController = rememberNavController()

   NavHost(
      navController = navController,
      startDestination = startDestination
   ) {

      composable<Screen.Auth> {
         AuthScreen(
            navigateToHoneScreen ={
               navController.navigate(Screen.HomeGraph) {
                  popUpTo<Screen.Auth> {
                     inclusive=true
                  }
               }
            }
         )
      }

      composable<Screen.HomeGraph> {
          HomeGraphScreen(
              navigateToAuth = {
                  navController.navigate(Screen.Auth) {
                      popUpTo<Screen.HomeGraph> {
                          inclusive = true
                      }
                  }
              },
              navigateToProfile = {
                  navController.navigate(Screen.Profile)
              },
              navigateToAdmin = {
                  navController.navigate(Screen.Admin)
              },
              navigateToDetails = {
                  navController.navigate(Screen.Details(it))
              },
              navigateToCategorySearch = { categoryName ->
                  navController.navigate(Screen.CategorySearch(categoryName))
              },
              navigateToCheckOut = { totalAmount->
                  navController.navigate(Screen.Checkout(totalAmount))
              }
          )
      }


      composable<Screen.Profile> {
         ProfileScreen(
             navigateBack = {
                 navController.navigateUp()
             }
         )
      }

       composable<Screen.Admin> {
           AdminScreen(
               navigateBack = {
                   navController.navigateUp()
               },
               navigateToManageProduct = {id->
                   navController.navigate(Screen.ManageProduct(id))
               }
           )
       }

       composable<Screen.ManageProduct> {
           val id = it.toRoute<Screen.ManageProduct>().id
           ManageProductScreen(
              id =   id,
               navigateBack = {
                   navController.navigateUp()
               }
           )
       }

       composable<Screen.Details> {
            DetailsScreen(
                navigateBack = {
                    navController.navigateUp()
                }
            )
       }

       composable<Screen.CategorySearch> {
           val category = ProductCategory.valueOf(
               it.toRoute<Screen.CategorySearch>().category
           )

           CategorySearchScreen(
               category = category,
               navigateToDetails = {id->
                   navController.navigate(Screen.Details(id))
               },
               navigateBack = { navController.navigateUp()}
           )
       }

       composable<Screen.Checkout> {

           val totalAmount = it.toRoute<Screen.Checkout>().totalAmount

           CheckoutScreen(
               navigateBack = {
                   navController.navigateUp()
               },
               totalAmount = totalAmount.toDoubleOrNull() ?: 0.0,
               navigateToPaymentCompleted = { isSuccess,error ->
                   navController.navigate(Screen.PaymentCompleted(isSuccess,error))
               },
           )
       }

       composable<Screen.PaymentCompleted> {
           val isSuccess= it.toRoute<Screen.PaymentCompleted>().isSuccess
           val error= it.toRoute<Screen.PaymentCompleted>().error
           PaymentCompleted(
               isSuccess = isSuccess,
               error = error,
               navigateBack = {
                   navController.navigate(Screen.HomeGraph) {
                       launchSingleTop=true
                       popUpTo(0) {
                           inclusive = true
                       }
                   }
               }
           )
       }

   }
}
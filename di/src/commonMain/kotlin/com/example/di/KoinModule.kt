package com.example.di

import com.example.admin.AdminPanelViewModel
import com.example.auth.AuthViewModel
import com.example.cart.CartViewModel
import com.example.categorysearch.CategoriesSearchViewModel
import com.example.data.AdminRepositoryImpl
import com.example.data.CustomerRepositoryImpl
import com.example.data.ProductRepositoryImpl
import com.example.data.domain.AdminRepository
import com.example.data.domain.CustomerRepository
import com.example.data.domain.ProductRepository
import com.example.details.DetailsViewModel
import com.example.home.HomeGraphViewModel
import com.example.manageproduct.ManageProductViewModel
import com.example.productsoverview.ProductsOverviewViewModel
import com.example.checkout.CheckoutViewModel
import com.example.checkout.domain.PayPalApi
import com.example.data.OrderRepositoryImpl
import com.example.data.domain.OrderRepository
import com.example.profile.ProfileViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedModule = module {
    single<CustomerRepository> {CustomerRepositoryImpl()}
    single<AdminRepository> {AdminRepositoryImpl()}
    single<ProductRepository> {ProductRepositoryImpl()}
    single<OrderRepository> { OrderRepositoryImpl(get()) }
    single<PayPalApi> { PayPalApi() }
    viewModelOf(::AuthViewModel)
    viewModelOf(::HomeGraphViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::ManageProductViewModel)
    viewModelOf(::AdminPanelViewModel)
    viewModelOf(::ProductsOverviewViewModel)
    viewModelOf(::DetailsViewModel)
    viewModelOf(::CartViewModel)
    viewModelOf(::CategoriesSearchViewModel)
    viewModelOf(::CheckoutViewModel)



}

expect val targetModule : Module


fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null,
) {
    startKoin {
        config?.invoke(this)
        modules(sharedModule, targetModule)
    }

}
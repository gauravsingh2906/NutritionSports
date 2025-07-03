package com.example.data.domain

import com.example.shared.domain.CartItem
import com.example.shared.domain.Customer
import com.example.shared.utils.RequestState
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {

    fun getCurrentUserId():String?

    suspend fun signOut(): RequestState<Unit>

    suspend fun createCustomer(
        user:FirebaseUser?=null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    suspend fun updateCustomer(
        customer: Customer,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    fun readCustomerFlow() : Flow<RequestState<Customer>>

    suspend fun addItemToCart(
        cartItem: CartItem,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    suspend fun updateCartItemQuantity(
        id:String,
        quantity:Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    suspend fun deleteCartItem(
        id:String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    suspend fun deleteAllCartItems(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )


}
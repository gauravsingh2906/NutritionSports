package com.example.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.domain.CustomerRepository
import com.example.data.domain.ProductRepository
import com.example.shared.utils.RequestState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(
    private val customerRepository: CustomerRepository,
    private val productRepository: ProductRepository
):ViewModel() {

  private val customer = customerRepository.readCustomerFlow()
       .stateIn(
           scope = viewModelScope,
           started = SharingStarted.WhileSubscribed(5000),
           initialValue = RequestState.Loading
       )

    @OptIn(ExperimentalCoroutinesApi::class)
   private val products = customer
        .flatMapLatest { customerState->
            if (customerState.isSuccess()) {
                val productIds = customerState.getSuccessData().cart.map { it.productId }.toSet()
                if (productIds.isNotEmpty()) {
                    productRepository.readProductByIdsFlows(productIds.toList())
                } else {
                    flowOf(RequestState.Success(emptyList()))
                }

            } else if (customerState.isError()) {
                flowOf(RequestState.Error(customerState.getErrorMessage()))
            } else flowOf(RequestState.Loading)
        }

    val cartItemsWithProducts = combine(customer,products) {customerState,productState->
        when {
            customerState.isSuccess() && productState.isSuccess() -> {
                val cart = customerState.getSuccessData().cart
                val products = productState.getSuccessData()

                val result = cart.mapNotNull { cartItem->
                    val product = products.find { it.id == cartItem.productId }
                    product?.let { cartItem to it }
                }

                RequestState.Success(result)
            }

            customerState.isError() -> RequestState.Error(customerState.getErrorMessage())
            productState.isError() -> RequestState.Error(productState.getErrorMessage())

            else -> RequestState.Loading
        }
    }



    fun updateCartItemQuantity(
        id:String,
        quantity:Int,
        onSuccess:() ->Unit,
        onError:(String) ->Unit
    ) {
        viewModelScope.launch {
            customerRepository.updateCartItemQuantity(
                id = id,
                quantity = quantity,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    fun deleteCartItem(
        id:String,
        onSuccess:() ->Unit,
        onError:(String) ->Unit
    ) {
        viewModelScope.launch {
           customerRepository.deleteCartItem(
               id = id,
               onError = onError,
               onSuccess = onSuccess
           )
        }
    }




}
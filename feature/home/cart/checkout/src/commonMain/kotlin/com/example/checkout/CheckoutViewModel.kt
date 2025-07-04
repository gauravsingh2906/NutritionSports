package com.example.checkout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkout.domain.Amount
import com.example.checkout.domain.PayPalApi
import com.example.checkout.domain.ShippingAddress
import com.example.data.domain.CustomerRepository
import com.example.data.domain.OrderRepository
import com.example.shared.domain.CartItem
import com.example.shared.domain.Country
import com.example.shared.domain.Customer
import com.example.shared.domain.Order
import com.example.shared.domain.PhoneNumber
import com.example.shared.utils.RequestState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


data class CheckoutScreenState(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val city: String? = null,
    val postalCode: Int? = null,
    val address: String? = null,
    val country: Country = Country.India,
    val phoneNumber: PhoneNumber? = null,
    val cart:List<CartItem> = emptyList()
)

class CheckoutViewModel(
    private val customerRepository: CustomerRepository,
    private val orderRepository: OrderRepository,
    private val payPal: PayPalApi,
    private val savedStateHandle: SavedStateHandle
):ViewModel() {

    var screenReady: RequestState<Unit> by mutableStateOf(RequestState.Loading)
    var screenState: CheckoutScreenState by mutableStateOf(CheckoutScreenState())
        private set

    val isFormValid: Boolean
        get() = with(screenState) {
            firstName.length in 3..50 &&
                    lastName.length in 3..50 &&
                    city?.length in 3..50 &&
                    postalCode!=null || postalCode.toString().length in 3..8 &&
                    address?.length in 3..50 &&
                    phoneNumber?.number?.length in 5..25
        }

       init {
           viewModelScope.launch {
               payPal.fetchAccessToken(
                   onSuccess = { token->
                        println("TOKEN RECEIVED: $token")
                   },
                   onError = { message->
                       println(message)
                   }
               )
           }


            viewModelScope.launch {

                customerRepository.readCustomerFlow().collectLatest { data ->
                    if (data.isSuccess()) {
                        val fetchedCustomer = data.getSuccessData()
                        screenState = CheckoutScreenState(
                            id = fetchedCustomer.id,
                            firstName = fetchedCustomer.firstName,
                            lastName = fetchedCustomer.lastName,
                            email = fetchedCustomer.email,
                            city = fetchedCustomer.city,
                            postalCode = fetchedCustomer.postalCode,
                            address = fetchedCustomer.address,
                            phoneNumber = fetchedCustomer.phoneNumber,
                            country = com.example.shared.domain.Country.entries.firstOrNull { it.dialCode == fetchedCustomer.phoneNumber?.dialCode }
                                ?: Country.India,
                            cart = fetchedCustomer.cart
                        )
                        screenReady = com.example.shared.utils.RequestState.Success(kotlin.Unit)
                    } else if (data.isError()) {
                        screenReady = com.example.shared.utils.RequestState.Error(data.getErrorMessage())
                    }
                }
            }
        }

    fun updateFirstName(value: String) {
        screenState = screenState.copy(firstName = value)
    }

    fun updateLastName(value: String) {
        screenState = screenState.copy(lastName = value)
    }

    fun updateCity(value: String) {
        screenState = screenState.copy(city = value)
    }

    fun updatePostalCode(value: Int?) {
        screenState = screenState.copy(postalCode = value)
    }

    fun updateAddress(value: String) {
        screenState = screenState.copy(address = value)
    }

    fun updateCountry(value: Country) {
        screenState = screenState.copy(
            country = value,
            phoneNumber = screenState.phoneNumber?.copy(
                dialCode = value.dialCode
            )
        )
    }

    fun updatePhoneNumber(value: String) {
        screenState = screenState.copy(
            phoneNumber = PhoneNumber(
                dialCode = screenState.country.dialCode,
                number = value
            )
        )
    }


    private fun updateCustomer(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            customerRepository.updateCustomer(
                customer = Customer(
                    id = screenState.id,
                    firstName = screenState.firstName,
                    lastName = screenState.lastName,
                    email = screenState.email,
                    city = screenState.city,
                    address = screenState.address,
                    postalCode = screenState.postalCode,
                    phoneNumber = screenState.phoneNumber
                ),
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    fun payOnDelivery(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        updateCustomer(
            onSuccess = {
                createOrder(
                    onSuccess = onSuccess,
                    onError = onError
                )
            },
            onError=onError
        )
    }

    private fun createOrder(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
       viewModelScope.launch {
           orderRepository.createTheOrder(
               order = Order(
                   customerId = screenState.id,
                   items = screenState.cart,
                   totalAmount = savedStateHandle.get<String>("totalAmount")?.toDoubleOrNull() ?: 0.0
               ),
               onSuccess = onSuccess,
               onError = onError
           )
       }
    }

    fun payWithPayPal(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val totalAmount = savedStateHandle.get<String>("totalAmount")

        if (totalAmount !=null) {
            viewModelScope.launch {
                payPal.beginCheckout(
                    amount = Amount(
                        currencyCode = "USD",
                        value = totalAmount
                    ),
                    fullName = "${screenState.firstName} ${screenState.lastName}",
                    shippingAddress = ShippingAddress(
                        addressLine1 = screenState.address ?: "Unknown Address",
                        city = screenState.city ?: "Unknown city",
                        state = screenState.country.name,
                        countryCode = screenState.country.code,
                        postalCode = screenState.postalCode.toString()
                    ),
                    onSuccess =onSuccess ,
                    onError = onError
                )
            }
        } else {
            onError("Total amount wouldn't be calculated.")
        }

    }


}
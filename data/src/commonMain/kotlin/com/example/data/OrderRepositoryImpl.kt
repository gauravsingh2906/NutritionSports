package com.example.data

import com.example.data.domain.CustomerRepository
import com.example.data.domain.OrderRepository
import com.example.shared.domain.CartItem
import com.example.shared.domain.Order
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

class OrderRepositoryImpl(
    private val customerRepository: CustomerRepository
) : OrderRepository {
    override fun getCurrentUserId() = Firebase.auth.currentUser?.uid

    override suspend fun createTheOrder(
        order: Order,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val database = Firebase.firestore
                val orderCollection = database.collection(collectionPath = "order")

                orderCollection.document(order.orderId).set(order)
                customerRepository.deleteAllCartItems(
                    onSuccess = {},
                    onError = onError
                )

                onSuccess()
            } else {
                onError("Select customer does not exist.")
            }

        } catch (e: Exception) {
            onError("Error while adding a product to cart: ${e.message}")
        }
    }


}


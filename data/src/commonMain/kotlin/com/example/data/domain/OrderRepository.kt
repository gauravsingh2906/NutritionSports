package com.example.data.domain

import com.example.shared.domain.Order

interface OrderRepository {

    fun getCurrentUserId():String?

    suspend fun createTheOrder(
        order: Order,
        onSuccess:()->Unit,
        onError:(String) ->Unit
    )

}
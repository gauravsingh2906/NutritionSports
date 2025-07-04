package com.example.data.domain

import com.example.shared.domain.Product
import com.example.shared.domain.ProductCategory
import com.example.shared.utils.RequestState
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getCurrentUserId():String?

    fun readDiscountedProducts(): Flow<RequestState<List<Product>>>


    fun readNewProducts(): Flow<RequestState<List<Product>>>

    fun readProductByIdFlow(id:String):Flow<RequestState<Product>>

    fun readProductByIdsFlows(ids:List<String>):Flow<RequestState<List<Product>>>

    fun readProductByCategoryFlow(category: ProductCategory):Flow<RequestState<List<Product>>>

}
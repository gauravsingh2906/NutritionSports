package com.example.data.domain

import com.example.shared.domain.Product
import com.example.shared.utils.RequestState
import kotlinx.coroutines.flow.Flow

interface AdminRepository {

    fun getCurrentUserId():String?

    suspend fun createNewProduct(
        product: Product,
        onSuccess:()->Unit,
        onError:(String) -> Unit
    )

    suspend fun uploadImageToSupabase(bytes:ByteArray):String?

    suspend fun deleteImageFromStorage(
        downloadUrl:String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    fun readLastTenProducts(): Flow<RequestState<List<Product>>>

    suspend fun readProductById(id:String): RequestState<Product>

    suspend fun updateImageThumbnail(
        productId:String,
        downloadUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    suspend fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    suspend fun deleteProduct(
        productId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    fun searchProductsByTitle(
        searchQuery:String
    ):Flow<RequestState<List<Product>>>

}
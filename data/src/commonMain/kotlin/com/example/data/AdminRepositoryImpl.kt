package com.example.data

import com.example.data.domain.AdminRepository
import com.example.shared.SupabaseObject

import com.example.shared.domain.Product
import com.example.shared.utils.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withTimeout
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class AdminRepositoryImpl : AdminRepository {
    override fun getCurrentUserId() = Firebase.auth.currentUser?.uid

    override suspend fun createNewProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()

            if (currentUserId != null) {
                val firestore = Firebase.firestore
                val productCollection = firestore.collection(collectionPath = "product")
                productCollection.document(product.id)
                    .set(product.copy(title = product.title.lowercase()))
                onSuccess()

            } else {
                onError("User is not available")
            }

        } catch (e: Exception) {
            onError("While creating a new product: ${e.message}")
        }
    }

    @OptIn(ExperimentalUuidApi::class, SupabaseInternal::class)
    override suspend fun uploadImageToSupabase(bytes: ByteArray): String? {


        return try {
            val fileName = "${Uuid.random().toHexString()}.jpg"
            val bucket = SupabaseObject.supabase.storage.from("images")

            bucket.upload(
                path = fileName,
                data = bytes,
                options = {
                    contentType = ContentType.Image.JPEG
                    upsert = true
                }
            )


            bucket.publicUrl(fileName)
        } catch (e: Exception) {
            println(e.message)
            RequestState.Error("it's the error: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    override suspend fun deleteImageFromStorage(
        downloadUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val prefix = "https://kivtzypuyiivcvlogqak.supabase.co/storage/v1/object/public/images/"
        val imagePath = downloadUrl.removePrefix(prefix).removePrefix("/")

        try {
            val bucket = SupabaseObject.supabase.storage["images"]
            withTimeout(20_0000L) {
                bucket.delete(listOf(imagePath))
            }
            onSuccess()

        } catch (e: Exception) {
            onError("Failed to delete image: ${e.message}")
        }


    }

    override fun readLastTenProducts(): Flow<RequestState<List<Product>>> = channelFlow {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                database.collection(collectionPath = "product")
                    .orderBy("createdAt", Direction.DESCENDING)
                    .limit(10)
                    .snapshots
                    .collectLatest { query ->
                        val products = query.documents.map { document ->
                            Product(
                                id = document.id,
                                title = document.get(field = "title"),
                                createdAt = document.get(field = "createdAt"),
                                description = document.get(field = "description"),
                                thumbnail = document.get(field = "thumbnail"),
                                category = document.get(field = "category"),
                                flavors = document.get(field = "flavors"),
                                weight = document.get(field = "weight"),
                                price = document.get(field = "price"),
                                isPopular = document.get(field = "isPopular"),
                                isDiscounted = document.get(field = "isDiscounted"),
                                isNew = document.get(field = "isNew")
                            )
                        }
                        send(RequestState.Success(data = products.map { it.copy(title = it.title.uppercase())  }))
                    }
            } else {
                send(RequestState.Error("User is not available."))
            }

        } catch (e: Exception) {
            send(RequestState.Error("Error while reading a last 10 items from the database: ${e.message}"))
        }
    }

    override suspend fun readProductById(id: String): RequestState<Product> {
        return try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                val productDocument = database.collection(collectionPath = "product")
                    .document(id)
                    .get()

                if (productDocument.exists) {
                    val product = Product(
                        id = productDocument.id,
                        title = productDocument.get(field = "title"),
                        createdAt = productDocument.get(field = "createdAt"),
                        description = productDocument.get(field = "description"),
                        thumbnail = productDocument.get(field = "thumbnail"),
                        category = productDocument.get(field = "category"),
                        flavors = productDocument.get(field = "flavors"),
                        weight = productDocument.get(field = "weight"),
                        price = productDocument.get(field = "price"),
                        isPopular = productDocument.get(field = "isPopular"),
                        isDiscounted = productDocument.get(field = "isDiscounted"),
                        isNew = productDocument.get(field = "isNew")
                    )
                    RequestState.Success(product.copy(title = product.title.uppercase()))
                } else {
                    RequestState.Error("Selected product not found.")
                }

            } else {
                RequestState.Error("User is not available.")
            }
        } catch (e: Exception) {
            RequestState.Error(" Error while reading a selected product: ${e.message}")
        }
    }

    override suspend fun updateImageThumbnail(
        productId: String,
        downloadUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {

                val database = Firebase.firestore
                val productCollection = database.collection("product")
                val existingProduct = productCollection
                    .document(productId)
                    .get()

                if (existingProduct.exists) {
                    productCollection.document(productId)
                        .update("thumbnail" to downloadUrl)
                    onSuccess()
                } else {
                    onError("Selected product not found.")
                }

            } else {
                onError("User is not available")
            }


        } catch (e: Exception) {
            onError("Error while updating a thumbnail image: ${e.message}")
        }
    }

    override suspend fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {

                val database = Firebase.firestore
                val productCollection = database.collection("product")
                val existingProduct = productCollection
                    .document(product.id)
                    .get()

                if (existingProduct.exists) {
                    productCollection.document(product.id)
                        .update(product.copy(title = product.title.lowercase()))
                    onSuccess()
                } else {
                    onError("Selected product not found.")
                }

            } else {
                onError("User is not available")
            }


        } catch (e: Exception) {
            onError("Error while updating a thumbnail image: ${e.message}")
        }
    }

    override suspend fun deleteProduct(
        productId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {

                val database = Firebase.firestore
                val productCollection = database.collection("product")
                val existingProduct = productCollection
                    .document(productId)
                    .get()

                if (existingProduct.exists) {
                    productCollection.document(productId)
                        .delete()
                    onSuccess()
                } else {
                    onError("Selected product not found.")
                }

            } else {
                onError("User is not available")
            }


        } catch (e: Exception) {
            onError("Error while updating a thumbnail image: ${e.message}")
        }
    }

    override fun searchProductsByTitle(searchQuery: String): Flow<RequestState<List<Product>>> =
        channelFlow {

            try {
                val userId = getCurrentUserId()
                if (userId != null) {
                    val database = Firebase.firestore

        //            val queryText = searchQuery.trim().lowercase()
       //             val endText = queryText + "\uf8ff"

                    database.collection("product")
//                        .orderBy("title")
//                        .startAt(queryText)
//                        .endAt(endText)
                        .snapshots
                        .collectLatest { query ->
                            val products = query.documents.map { productDocument ->
                                Product(
                                    id = productDocument.id,
                                    title = productDocument.get(field = "title"),
                                    createdAt = productDocument.get(field = "createdAt"),
                                    description = productDocument.get(field = "description"),
                                    thumbnail = productDocument.get(field = "thumbnail"),
                                    category = productDocument.get(field = "category"),
                                    flavors = productDocument.get(field = "flavors"),
                                    weight = productDocument.get(field = "weight"),
                                    price = productDocument.get(field = "price"),
                                    isPopular = productDocument.get(field = "isPopular"),
                                    isDiscounted = productDocument.get(field = "isDiscounted"),
                                    isNew = productDocument.get(field = "isNew")
                                )
                            }
                            send(RequestState.Success(
                                products
                                    .filter { it.title.contains(searchQuery) }
                                    .map { it.copy(title = it.title.uppercase()) }
                            ))
                        }


                } else {
                    send(RequestState.Error("User is not available."))
                }


            } catch (e: Exception) {
                send(RequestState.Error("Error while searching products: ${e.message}"))
            }


        }


}
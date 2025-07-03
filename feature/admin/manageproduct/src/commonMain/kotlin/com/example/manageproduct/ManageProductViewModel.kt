package com.example.manageproduct

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.domain.AdminRepository

import com.example.shared.domain.Product
import com.example.shared.domain.ProductCategory
import com.example.shared.utils.RequestState

import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class ManageProductState(
    val id: String = Uuid.random().toHexString(),
    val createdAt:Long = Clock.System.now().toEpochMilliseconds(),
    val title: String = "",
    val description: String = "",
    val thumbnail: String = "",
    val category: ProductCategory = ProductCategory.Protein,
    val flavors: String = "",
    val weight: Int? = null,
    val price: Double = 0.0,
    val isNew:Boolean = false,
    val isPopular:Boolean = false,
    val isDiscounted:Boolean = false,
)

class ManageProductViewModel(
    private val adminRepository: AdminRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId = savedStateHandle.get<String>("id") ?: ""

    var screenState by mutableStateOf(ManageProductState())
        private set

    var thumbnailUploaderState: RequestState<Unit> by mutableStateOf(RequestState.Idle)
        private set


    val isFormValid: Boolean
        get() = screenState.title.isNotEmpty() &&
                screenState.description.isNotEmpty() &&
                screenState.thumbnail.isNotEmpty() &&
                screenState.price != 0.0


    init {
        productId.takeIf { it.isNotEmpty() }?.let { id->
            viewModelScope.launch {
                val selectedProduct = adminRepository.readProductById(id)
                if(selectedProduct.isSuccess()) {
                    val product = selectedProduct.getSuccessData()

                    updateId(product.id)
                    updateCreatedAt(product.createdAt)
                    updateTitle(product.title)
                    updateDescription(product.description)
                    updateThumbnail(product.thumbnail)
                    updateThumbnailUploaderState(RequestState.Success(Unit))
                    updateCategory(ProductCategory.valueOf(product.category))
                    updateFlavors(product.flavors?.joinToString(",") ?: "")
                    updateWeight(product.weight)
                    updatePrice(product.price)
                    updateNew(product.isNew)
                    updatePopular(product.isPopular)
                    updateDiscounted(product.isDiscounted)
                }

            }
        }

    }

    fun updateId(value: String) {
        screenState = screenState.copy(id = value)
    }

    fun updateCreatedAt(value: Long) {
        screenState = screenState.copy(createdAt = value)
    }


    fun updateTitle(value: String) {
        screenState = screenState.copy(title = value)
    }

    fun updateDescription(value: String) {
        screenState = screenState.copy(description = value)
    }

    fun updateThumbnail(value: String) {
        screenState = screenState.copy(thumbnail = value)
    }

    fun updateThumbnailUploaderState(value: RequestState<Unit>) {
        thumbnailUploaderState = value
    }

    fun updateCategory(value: ProductCategory) {
        screenState = screenState.copy(category = value)
    }

    fun updateFlavors(value: String) {
        screenState = screenState.copy(flavors = value)
    }

    fun updateWeight(value: Int?) {
        screenState = screenState.copy(weight = value)
    }

    fun updatePrice(value: Double) {
        screenState = screenState.copy(price = value)
    }

    fun updateNew(value: Boolean) {
        screenState = screenState.copy(isNew = value)
    }

    fun updatePopular(value: Boolean) {
        screenState = screenState.copy(isPopular = value)
    }

    fun updateDiscounted(value: Boolean) {
        screenState = screenState.copy(isDiscounted = value)
    }

//    fun createBucket(name:String,onSuccess: () -> Unit) {
//        viewModelScope.launch {
//            try {
//                updateThumbnailUploaderState(RequestState.Loading)
//                supabase.storage.createBucket(id = name) {
//                    public = true
//                    fileSizeLimit = 10.megabytes
//                }
//                onSuccess()
//            } catch (e:Exception) {
//                updateThumbnailUploaderState(RequestState.Error("Error while uploading : ${e.message}"))
//            }
//        }
//    }

    fun createNewProduct(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            adminRepository.createNewProduct(
                product = Product(
                    id = screenState.id,
                    createdAt = screenState.createdAt,
                    title = screenState.title,
                    description = screenState.description,
                    thumbnail = screenState.thumbnail,
                    category = screenState.category.name,
                    flavors = screenState.flavors.split(","),
                    weight = screenState.weight,
                    price = screenState.price,
                    isNew = screenState.isNew,
                    isPopular = screenState.isPopular,
                    isDiscounted = screenState.isDiscounted
                ),
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    fun uploadThumbnailToSupabase(
        bytes: ByteArray,
        onSuccess: (String) -> Unit,
    ) {

        updateThumbnailUploaderState(RequestState.Loading)

        viewModelScope.launch {
            try {
                val downoadUrl = adminRepository.uploadImageToSupabase(bytes)
                println("Uploaded image URL: $downoadUrl")

                if (downoadUrl.isNullOrEmpty()) {
                    throw Exception("Failed to retrieve a download URL after the upload")
                }

                productId.takeIf { it.isNotEmpty() }?.let { id->

                    adminRepository.updateImageThumbnail(
                        productId = id,
                        downloadUrl = downoadUrl,
                        onSuccess = {
                            updateThumbnail(downoadUrl)
                            updateThumbnailUploaderState(RequestState.Success(Unit))
                            onSuccess(downoadUrl)
                        },
                        onError = { message->
                            updateThumbnailUploaderState(RequestState.Error(message))
                        }
                    )
                } ?: run {
                    updateThumbnail(downoadUrl)
                    updateThumbnailUploaderState(RequestState.Success(Unit))
                    onSuccess(downoadUrl)
                }


            } catch (e: Exception) {
                updateThumbnailUploaderState(RequestState.Error("Error while uploading : ${e.message}"))
            }
        }
    }

    fun updateProduct(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (isFormValid) {

            viewModelScope.launch {
                adminRepository.updateProduct(
                    product = Product(
                        id = screenState.id,
                        createdAt = screenState.createdAt,
                        title = screenState.title,
                        description = screenState.description,
                        thumbnail = screenState.thumbnail,
                        category = screenState.category.name,
                        flavors = screenState.flavors.split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() },
                        weight = screenState.weight,
                        price = screenState.price,
                        isNew = screenState.isNew,
                        isPopular = screenState.isPopular,
                        isDiscounted = screenState.isDiscounted
                    ),
                    onSuccess = onSuccess,
                    onError = onError
                )
            }


        } else {
            onError("Please fill all information")
        }
    }

    fun deleteThumbnailFromSupabase(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            adminRepository.deleteImageFromStorage(
                downloadUrl = screenState.thumbnail,
                onSuccess = {
                    productId.takeIf { it.isNotEmpty() }?.let { id->
                        viewModelScope.launch {
                            adminRepository.updateImageThumbnail(
                                productId=id,
                                downloadUrl = "",
                                onSuccess = {
                                    updateThumbnail("")
                                    updateThumbnailUploaderState(RequestState.Idle)
                                    onSuccess()
                                },
                                onError = {message-> onError(message)}
                            )
                        }
                    } ?: run {
                        updateThumbnail("")
                        updateThumbnailUploaderState(RequestState.Idle)
                        onSuccess()
                    }

                },
                onError = onError
            )
        }
    }

    fun deleteProduct(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        productId.takeIf { it.isNotEmpty() }?.let { id ->
            viewModelScope.launch {
                adminRepository.deleteProduct(
                    productId = id,
                    onSuccess = {
                        deleteThumbnailFromSupabase(
                            onSuccess = {},
                            onError = {meesage->
                                onError(meesage)
                            }
                        )
                        onSuccess()
                    },
                    onError = { message ->
                        onError(message)
                    }
                )
            }
        }
    }


}
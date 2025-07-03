package com.example.shared.domain

import androidx.compose.ui.graphics.Color
import com.example.shared.CategoryBlue
import com.example.shared.CategoryGreen
import com.example.shared.CategoryPurple
import com.example.shared.CategoryRed
import com.example.shared.CategoryYellow
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id:String,
    val createdAt:Long = Clock.System.now().toEpochMilliseconds(),
    val title:String,
    val description:String,
    val thumbnail:String,
    val category:String,
    val flavors:List<String>?= null,
    val weight:Int?= null,
    val price:Double,
    val isPopular:Boolean =false,
    val isDiscounted:Boolean =false,
    val isNew:Boolean =false
)


enum class ProductCategory(
    val title:String,
    val color:Color
) {

    Protein(
        title = "Protein",
        color = CategoryYellow
    ),
    Creatine(
        title = "Creatine",
        color = CategoryBlue
    ),
    PreWorkout(
        title = "Pre-Workout",
        color = CategoryGreen
    ),
    Gainers(
        title = "Gainers",
        color = CategoryPurple
    ),
    Accessories(
        title = "Accessories",
        color = CategoryRed
    ),



}
package com.example.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.categories.components.CategoryCard
import com.example.shared.domain.ProductCategory

@Composable
fun CategoryScreen(
    navigateToCategoriesSearch:(String)->Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProductCategory.entries.forEach { category->
            CategoryCard(
                category = category,
                onClick = {
                    navigateToCategoriesSearch(category.name)
                }
            )
        }

    }
}
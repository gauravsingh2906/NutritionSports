rootProject.name = "NutritionSports"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        google()
        mavenCentral()
    }
}
include(":data")
include(":di")
include(":feature:admin")
include(":feature:admin:manageproduct")
include(":feature:auth")
include(":feature:details")
include(":feature:details")
include(":feature:home")
include(":feature:home:cart")
include(":feature:home:cart:checkout")
include(":feature:home:categories")
include(":feature:home:categories:categorysearch")
include(":feature:home:productsoverview")
include(":feature:paymentcompleted")
include(":feature:profile")
include(":navigation")


include(":shared")
include(":composeApp")
include(":shared")

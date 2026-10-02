package com.hightech.foodguard.network

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("product") val product: Product?
)

data class Product(
    @SerializedName("product_name") val productName: String?,
    @SerializedName("brands") val brands: String?,
    @SerializedName("image_front_url") val imageUrl: String?,
    @SerializedName("ingredients_text") val ingredientsText: String?,
    @SerializedName("allergens_tags") val allergensTags: List<String>?,
    @SerializedName("traces_tags") val tracesTags: List<String>?,
    @SerializedName("categories") val categories: String?
)

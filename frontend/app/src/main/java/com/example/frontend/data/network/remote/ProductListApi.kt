package com.example.frontend.data.network.remote

import com.example.frontend.data.model.ProductDetailDto
import com.example.frontend.data.model.ProductListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductListApi {

    @GET("api/products")
    suspend fun getProducts(
        @Query("search")    search:   String? = null,
        @Query("type")      type:     String? = null,
        @Query("after_use") afterUse: String? = null,
        @Query("limit") limit: Int = 20
    ): Response<ProductListResponse>

    @GET("api/products/tags")
    suspend fun getTags(): Response<List<String>>

    @GET("api/products/{id}")
    suspend fun getProductById(
        @Path("id") id: Long
    ): Response<ProductDetailDto>
}
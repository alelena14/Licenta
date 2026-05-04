package com.licenta.licenta_backend.repository

import com.licenta.licenta_backend.model.*
import org.springframework.data.jpa.repository.JpaRepository

interface ProductIngredientRepository :
    JpaRepository<ProductIngredient, ProductIngredientId> {

    fun findByProductId(productId: Long): List<ProductIngredient>

    fun existsByProductAndIngredient(
        product: Product,
        ingredient: Ingredient
    ): Boolean
}
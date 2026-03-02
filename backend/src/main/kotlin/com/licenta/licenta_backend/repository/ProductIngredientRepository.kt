package com.licenta.licenta_backend.repository

import com.licenta.licenta_backend.model.Ingredient
import com.licenta.licenta_backend.model.ProductIngredient
import org.springframework.data.jpa.repository.JpaRepository

interface ProductIngredientRepository : JpaRepository<ProductIngredient, Long> {

    fun findByProductId(productId: Long): List<ProductIngredient>

}

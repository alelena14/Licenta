package com.licenta.licenta_backend.service

import com.licenta.licenta_backend.model.Ingredient
import com.licenta.licenta_backend.repository.IngredientRepository
import com.licenta.licenta_backend.repository.ProductIngredientRepository
import org.springframework.stereotype.Service

@Service
class ProductIngredientService(
    private val productIngredientRepository: ProductIngredientRepository,
    private val ingredientRepository: IngredientRepository
) {

    fun getIngredientsForProduct(productId: Long): List<Ingredient> {

        val productIngredients = productIngredientRepository.findByProductId(productId)

        if (productIngredients.isEmpty()) return emptyList()

        val ingredientIds = productIngredients.map { it.id.ingredientId }

        return ingredientRepository.findAllById(ingredientIds)
    }
}
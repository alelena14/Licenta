package com.licenta.licenta_backend.repository

import com.licenta.licenta_backend.model.Ingredient
import org.springframework.data.jpa.repository.JpaRepository

interface IngredientRepository : JpaRepository<Ingredient, Long> {
    fun findByName(name: String): Ingredient?
}

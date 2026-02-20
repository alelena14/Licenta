package com.licenta.licenta_backend.repository

import com.licenta.licenta_backend.model.Ingredient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface IngredientRepository : JpaRepository<Ingredient, Long> {

    @Query(
        value = """
    SELECT i.*
    FROM ingredients i
    JOIN product_ingredient pi ON pi.ingredient_id = i.id
    WHERE i.category IS NULL
    GROUP BY i.id
    ORDER BY COUNT(pi.product_id) DESC
    LIMIT :limit
    """,
        nativeQuery = true
    )
    fun findTopUnenrichedIngredients(@Param("limit") limit: Int): List<Ingredient>

    fun findByName(name: String): Ingredient?
}


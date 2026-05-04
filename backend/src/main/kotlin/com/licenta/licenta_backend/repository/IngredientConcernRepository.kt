package com.licenta.licenta_backend.repository

import com.licenta.licenta_backend.model.IngredientConcern
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface IngredientConcernRepository : JpaRepository<IngredientConcern, Long> {

    fun findByIngredientId(ingredientId: Long): List<IngredientConcern>

    fun existsByIngredientIdAndConcernId(ingredientId: Long, concernId: Long): Boolean

    fun findByIngredientIdAndConcernId(ingredientId: Long, concernId: Long): IngredientConcern?


    @Query("SELECT ic FROM IngredientConcern ic WHERE ic.needsReview = true")
    fun findAllNeedingReview(): List<IngredientConcern>

    fun deleteByIngredientId(ingredientId: Long)

    @Query("SELECT ic FROM IngredientConcern ic WHERE ic.concernId IN :concernIds")
   fun findByConcernIdIn(@Param("concernIds") concernIds: List<Long>): List<IngredientConcern>
}
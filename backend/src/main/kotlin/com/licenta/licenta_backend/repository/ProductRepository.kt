package com.licenta.licenta_backend.repository

import com.licenta.licenta_backend.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<Product, Long> {

    fun findAllByUrlIsNull(): List<Product>

    fun findByNameAndBrand(name: String, brand: String): Product?

    fun existsByNameAndBrand(name: String, brand: String): Boolean

    @Query("""
    SELECT DISTINCT p FROM Product p
    LEFT JOIN FETCH p.ingredients pi
    LEFT JOIN FETCH pi.ingredient
    WHERE p.area = :area
""")
    fun findByAreaWithIngredients(@Param("area") area: String): List<Product>
}
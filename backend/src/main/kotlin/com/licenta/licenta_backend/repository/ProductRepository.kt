package com.licenta.licenta_backend.repository

import com.licenta.licenta_backend.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph

interface ProductRepository : JpaRepository<Product, Long> {


    fun findAllByUrlIsNull(): List<Product>

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.afterUse")
    fun findAllWithAfterUse(): List<Product>

    @EntityGraph(attributePaths = ["afterUse"])
    @Query("""
    SELECT p FROM Product p
""")
    fun findTopProducts(
        pageable: Pageable
    ): List<Product>


    @EntityGraph(attributePaths = ["afterUse"])
    @Query("""
    SELECT p FROM Product p
    WHERE
        LOWER(p.name) LIKE CONCAT('%', :search, '%')
        OR LOWER(p.brand) LIKE CONCAT('%', :search, '%')
""")
    fun searchProducts(
        @Param("search") search: String,
        pageable: Pageable
    ): List<Product>

    @EntityGraph(attributePaths = ["afterUse"])
    @Query("""
    SELECT p FROM Product p
    WHERE LOWER(p.type) = :type
""")
    fun findByType(
        @Param("type") type: String,
        pageable: Pageable
    ): List<Product>


    @EntityGraph(attributePaths = ["afterUse"])
    @Query("""
    SELECT DISTINCT p FROM Product p
    JOIN p.afterUse au
    WHERE LOWER(au.label) = :afterUse
""")
    fun findByAfterUse(
        @Param("afterUse") afterUse: String,
        pageable: Pageable
    ): List<Product>


    @Query("""
    SELECT DISTINCT p FROM Product p
    LEFT JOIN FETCH p.afterUse
    LEFT JOIN FETCH p.ingredients pi
    LEFT JOIN FETCH pi.ingredient
    WHERE p.id = :id
""")
    fun findDetailedById(
        @Param("id") id: Long
    ): Product?


    @Query("""
    SELECT au.label
    FROM Product p
    JOIN p.afterUse au
    WHERE p.id = :id
""")
    fun findAfterUseLabelsByProductId(
        @Param("id") id: Long
    ): List<String>

    @Query(
        value = """
        SELECT i.name
        FROM product_ingredient pi
        JOIN ingredients i ON pi.ingredient_id = i.id
        WHERE pi.product_id = :id
    """,
        nativeQuery = true
    )
    fun findIngredients(
        @Param("id") id: Long
    ): List<String>

    @Query("""
    SELECT DISTINCT p FROM Product p
    LEFT JOIN FETCH p.ingredients pi
    LEFT JOIN FETCH pi.ingredient
    WHERE p.area = :area
""")
    fun findByAreaWithIngredients(@Param("area") area: String): List<Product>

    @Query("""
    SELECT DISTINCT p FROM Product p
    LEFT JOIN FETCH p.afterUse a
    WHERE (:search IS NULL OR LOWER(p.name) LIKE %:search% OR LOWER(p.brand) LIKE %:search%)
    AND   (:type IS NULL OR LOWER(p.type) = :type)
    AND   (:afterUse IS NULL OR LOWER(a.label) = :afterUse)
""")
    fun searchFiltered(
        @Param("search")   search:   String?,
        @Param("type")     type:     String?,
        @Param("afterUse") afterUse: String?,
        pageable: Pageable
    ): List<Product>
}
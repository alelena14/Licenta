package com.licenta.licenta_backend.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository
import com.licenta.licenta_backend.model.Product

@Repository
interface RecommendationRepository : JpaRepository<Product, Long> {

    @Query(
        value = """
        SELECT p.* FROM products p
        JOIN product_after_use au ON au.product_id = p.id
        JOIN concern_tag_map ctm ON ctm.tag_id = au.after_use_id
        JOIN concerns c ON c.id = ctm.concern_id
        WHERE c.code IN (:codes)
        GROUP BY p.id
        HAVING SUM(ctm.weight) > 0
        ORDER BY SUM(ctm.weight) / SQRT(COUNT(au.after_use_id)) DESC
        LIMIT 10
        """,
        nativeQuery = true
    )
    fun findRecommendedProducts(@Param("codes") codes: List<String>): List<Product>
}
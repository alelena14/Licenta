package com.licenta.licenta_backend.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository
import com.licenta.licenta_backend.model.Product

@Repository
interface RecommendationRepository : JpaRepository<Product, Long> {

    @Query("""
    SELECT DISTINCT p FROM Product p
    LEFT JOIN FETCH p.afterUse
    WHERE p.area = :area
    AND p.id IN (
        SELECT p2.id FROM Product p2
        JOIN p2.afterUse au
        JOIN ConcernTagMap ctm ON ctm.id.tagId = au.id
        JOIN Concern c ON c.id = ctm.id.concernId
        WHERE c.code IN :codes
    )
    """)
    fun findCandidateProducts(
        @Param("codes") codes: List<String>,
        @Param("area") area: String
    ): List<Product>

    @Query(
        value = """
    SELECT
        p.id AS product_id,
        (
            (
                SUM(1 - (i.embedding <=> c.embedding))
                / SQRT(COUNT(i.id))
            )
            - (COALESCE(AVG(i.comedogenic_rating), 0) * 0.8)
            - (
                COUNT(
                    CASE 
                        WHEN i.irritation_potential = 'high'
                        THEN 1 
                    END
                ) * 0.6
              )
        ) AS final_score
    FROM products p
    JOIN product_ingredient pi ON pi.product_id = p.id
    JOIN ingredients i ON i.id = pi.ingredient_id
    JOIN concerns c ON c.id IN (:concernIds)
    WHERE p.area = :area
      AND (1 - (i.embedding <=> c.embedding)) > 0.45
    GROUP BY p.id
    ORDER BY final_score DESC
    """,
        nativeQuery = true
    )
    fun findSemanticScores(
        @Param("concernIds") concernIds: List<Long>,
        @Param("area") area: String
    ): List<SemanticScoreProjection>

}

interface SemanticScoreProjection {
    fun getProductId(): Long
    fun getSemanticScore(): Double?
}
package com.licenta.licenta_backend.repository

import com.licenta.licenta_backend.model.ProductFeatures
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProductFeaturesRepository :
    JpaRepository<ProductFeatures, Long> {

    @Query("""
        select pf from ProductFeatures pf
        join fetch Product p on p.id = pf.productId
    """)
    fun findAllWithProduct(): List<ProductFeatures>
}
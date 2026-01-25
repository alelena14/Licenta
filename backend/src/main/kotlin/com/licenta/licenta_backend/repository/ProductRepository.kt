package com.licenta.licenta_backend.repository
import com.licenta.licenta_backend.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ProductRepository : JpaRepository<Product, Long> {

    @Query("SELECT p FROM products p WHERE p.url IS NULL")
    fun findAllWithoutImage(): List<Product>
}

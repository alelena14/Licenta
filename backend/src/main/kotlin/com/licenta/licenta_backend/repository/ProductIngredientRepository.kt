package com.licenta.licenta_backend.repository

import com.licenta.licenta_backend.model.ProductIngredient
import org.springframework.data.jpa.repository.JpaRepository

interface ProductIngredientRepository : JpaRepository<ProductIngredient, Long>

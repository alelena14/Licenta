package com.licenta.licenta_backend.service

import com.licenta.licenta_backend.dto.ProductCardDto
import com.licenta.licenta_backend.dto.ProductDetailDto
import com.licenta.licenta_backend.dto.ProductListResponse
import com.licenta.licenta_backend.repository.AfterUseRepository
import com.licenta.licenta_backend.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

@Service
class ProductListService(
    private val productRepository: ProductRepository,
    private val afterUseRepository: AfterUseRepository
) {

    @Transactional
    fun getProducts(search: String?, type: String?, afterUse: String?, limit: Int = 20): ProductListResponse {
        val pageable = PageRequest.of(0, limit)

        val products = productRepository.searchFiltered(
            search   = search?.lowercase()?.takeIf { it.isNotBlank() },
            type     = type?.lowercase()?.takeIf { it.isNotBlank() },
            afterUse = afterUse?.lowercase()?.takeIf { it.isNotBlank() },
            pageable = pageable
        )

        val dtos = products.map { product ->
            ProductCardDto(
                id       = product.id,
                brand    = product.brand,
                name     = product.name,
                type     = product.type,
                area     = product.area,
                country  = product.country,
                url      = product.url,
                afterUse = product.afterUse.map { it.label }
            )
        }

        return ProductListResponse(
            products = dtos,
            total = dtos.size
        )
    }

    fun getAfterUseTags(): List<String> =
        afterUseRepository.findAllLabels()


    fun getProductById(id: Long): ProductDetailDto {
        val product = productRepository.findDetailedById(id)
            ?: throw IllegalArgumentException("Product not found.")
        return ProductDetailDto(
            id          = product.id,
            brand       = product.brand,
            name        = product.name,
            type        = product.type,
            area        = product.area,
            country     = product.country,
            url         = product.url,
            afterUse    = product.afterUse.map { it.label },
            ingredients = product.ingredients.map { it.ingredient.name }
        )
    }
}
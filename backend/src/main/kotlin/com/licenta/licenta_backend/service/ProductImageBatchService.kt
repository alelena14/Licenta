package com.licenta.licenta_backend.service

import com.licenta.licenta_backend.repository.ProductRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ProductImageBatchService(
    private val productRepository: ProductRepository,
    private val googleImageSearchService: GoogleImageSearchService
) {

    @Transactional
    fun enrichProductsWithImages(limit: Int = 100) {
        val products = productRepository.findAllWithoutImage()
            .take(limit)

        products.forEach { product ->
            val query = "${product.brand} ${product.name} skincare product"

            val url = googleImageSearchService.findBestImage(query)

            if (!url.isNullOrBlank()) {
                product.url = url
            }

            Thread.sleep(1000)
        }

        productRepository.saveAll(products)
    }
}

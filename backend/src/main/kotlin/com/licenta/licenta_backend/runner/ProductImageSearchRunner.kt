package com.licenta.licenta_backend.runner

import com.licenta.licenta_backend.repository.ProductRepository
import com.licenta.licenta_backend.service.ProductImageSearchService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = "app.image-runner",
    name = ["enabled"],
    havingValue = "true"
)
class ProductImageSearchRunner(
    private val productRepository: ProductRepository,
    private val imageSearchService: ProductImageSearchService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        val productsWithoutImage = productRepository.findAllByUrlIsNull()

        println("Found ${productsWithoutImage.size} products without image")

        val MAX_REQUESTS = 200
        var counter = 0

        for (product in productsWithoutImage) {
            if (counter >= MAX_REQUESTS) break

            try {
                val imageUrl = imageSearchService.findProductImage(
                    product.brand,
                    product.name
                )

                if (imageUrl != null) {
                    product.url = imageUrl
                    productRepository.save(product)
                    println("Image added for ${product.brand} ${product.name}")
                } else {
                    println("No image found for ${product.brand} ${product.name}")
                }

                counter++
                Thread.sleep(1000)

            } catch (ex: Exception) {
                println("Error for ${product.brand} ${product.name}: ${ex.message}")
            }
        }
    }
}


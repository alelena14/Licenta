package com.licenta.licenta_backend.controller

import com.licenta.licenta_backend.dto.ProductDetailDto
import com.licenta.licenta_backend.dto.ProductListResponse
import com.licenta.licenta_backend.service.ProductListService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductListController(
    private val productListService: ProductListService
) {

    @GetMapping
    fun getProducts(
        @RequestParam(required = false) search:     String?,
        @RequestParam(required = false) type:       String?,
        @RequestParam(name = "after_use", required = false) afterUse: String?,
        @RequestParam(required = false) limit:      Int?
    ): ResponseEntity<ProductListResponse> {
        return ResponseEntity.ok(productListService.getProducts(search, type, afterUse))
    }

    @GetMapping("/tags")
    fun getTags(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(productListService.getAfterUseTags())
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ResponseEntity<ProductDetailDto> {
        return ResponseEntity.ok(productListService.getProductById(id))
    }
}
package com.example.frontend.presentation.product

import com.example.frontend.data.model.ProductRecommendation
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory store for the currently selected product.
 * Injected as a singleton so ChatViewModel and ProductViewModel share the same instance.
 */
@Singleton
class ProductStore @Inject constructor() {
    var selectedProduct:   ProductRecommendation? = null
    var selectedScore:     Double?      = null
    var selectedConcerns:  List<String> = emptyList()
    var pendingProductId:  Long?        = null

    fun openFromChat(product: ProductRecommendation, score: Double?, concerns: List<String>) {
        selectedProduct  = product
        selectedScore    = score
        selectedConcerns = concerns
        pendingProductId = null
    }

    // Din Chat fara score/concerns
    fun openNormally(product: ProductRecommendation) {
        selectedProduct  = product
        selectedScore    = null
        selectedConcerns = emptyList()
        pendingProductId = null
    }

    // Din ProductListScreen — doar id, produsul se incarca în ViewModel
    fun openFromList(productId: Long) {
        pendingProductId = productId
        selectedProduct  = null
        selectedScore    = null
        selectedConcerns = emptyList()
    }
}
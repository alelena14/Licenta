package com.example.frontend.presentation.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.ProductRecommendation
import com.example.frontend.data.network.remote.ProductListApi
import com.example.frontend.domain.repository.FavoriteRepository
import com.example.frontend.domain.repository.FavoriteResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Save state ────────────────────────────────────────────────────────────────

sealed class SaveState {
    object Idle    : SaveState()
    object Loading : SaveState()
    object Saved   : SaveState()
    object Removed : SaveState()
    data class Error(val message: String) : SaveState()
}

@HiltViewModel
class ProductViewModel @Inject constructor(
    val store: ProductStore,
    private val favoriteRepository: FavoriteRepository,
    private val productListApi: ProductListApi
) : ViewModel() {

    private val _product = MutableStateFlow<ProductRecommendation?>(null)
    val product: StateFlow<ProductRecommendation?> = _product

    val score:    Double?      get() = store.selectedScore
    val concerns: List<String> get() = store.selectedConcerns

    private val _saveState  = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    init {
        val pendingId = store.pendingProductId
        if (pendingId != null) {
            loadProduct(pendingId)
        } else {
            _product.value = store.selectedProduct
            checkIfFavorite()
        }
    }

    private fun loadProduct(id: Long) {
        viewModelScope.launch {
            try {
                val response = productListApi.getProductById(id)
                if (response.isSuccessful && response.body() != null) {
                    val dto = response.body()!!
                    val product = ProductRecommendation(
                        id          = dto.id,
                        brand       = dto.brand,
                        name        = dto.name,
                        type        = dto.type,
                        score       = 0.0,
                        tags        = dto.afterUse,
                        ingredients = dto.ingredients,
                        warnings    = emptyList(),
                        explanation = "",
                        country     = dto.country,
                        url         = dto.url
                    )
                    store.selectedProduct = product
                    _product.value = product
                    checkIfFavorite()
                }
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    private fun checkIfFavorite() {
        val productId = store.selectedProduct?.id ?: return
        viewModelScope.launch {
            _isFavorite.value = favoriteRepository.isFavorite(productId)
        }
    }

    fun toggleFavorite() {
        val currentProduct = store.selectedProduct ?: return

        viewModelScope.launch {
            _saveState.value = SaveState.Loading

            if (_isFavorite.value) {
                when (val result = favoriteRepository.removeFavorite(currentProduct.id)) {
                    is FavoriteResult.Success -> {
                        _isFavorite.value = false
                        _saveState.value  = SaveState.Removed
                    }
                    is FavoriteResult.Error -> {
                        Log.e("FAVORITE", "toggle error: ${result.message}")
                        _saveState.value = SaveState.Error(result.message)
                    }
                }
            } else {
                when (val result = favoriteRepository.saveFavorite(
                    productId   = currentProduct.id,
                    score       = store.selectedScore,
                    concerns    = store.selectedConcerns,
                    explanation = currentProduct.explanation.takeIf { it.isNotBlank() }
                )) {
                    is FavoriteResult.Success -> {
                        _isFavorite.value = true
                        _saveState.value  = SaveState.Saved
                    }
                    is FavoriteResult.Error -> {
                        _saveState.value = SaveState.Error(result.message)
                    }
                }
            }

            kotlinx.coroutines.delay(2_000)
            _saveState.value = SaveState.Idle
        }
    }
}
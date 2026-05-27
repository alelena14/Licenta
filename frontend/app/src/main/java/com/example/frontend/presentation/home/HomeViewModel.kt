package com.example.frontend.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.ProductRecommendation
import com.example.frontend.domain.repository.HomeRepository
import com.example.frontend.presentation.product.ProductStore
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val auth: FirebaseAuth,
    val productStore: ProductStore
) : ViewModel() {

    sealed class State {
        object Loading : State()
        data class Success(val products: List<ProductRecommendation>) : State()
        object Empty : State()
        data class Error(val message: String) : State()
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    val username: String?
        get() = auth.currentUser?.displayName

    init {
        Log.d("TOKEN", "uid: ${auth.currentUser?.uid}")
        viewModelScope.launch {
            val token = auth.currentUser?.getIdToken(true)?.await()?.token
            Log.d("TOKEN", "token: $token")
        }
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                val products = homeRepository.getRecommendations()
                _state.value = if (products.isEmpty()) State.Empty
                else State.Success(products)
            } catch (e: Exception) {
                _state.value = State.Empty
            }
        }
    }
}
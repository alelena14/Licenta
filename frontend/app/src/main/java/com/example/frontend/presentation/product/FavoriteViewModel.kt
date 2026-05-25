package com.example.frontend.presentation.product

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.SavedProductDto
import com.example.frontend.domain.repository.FavoriteRepository
import com.example.frontend.domain.repository.FavoriteResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    val productStore: ProductStore
) : ViewModel() {

    sealed class State {
        object Loading : State()
        data class Success(val products: List<SavedProductDto>) : State()
        data class Error(val message: String) : State()
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()  // () lipsea

    var search by mutableStateOf("")
        private set

    val filtered: StateFlow<List<SavedProductDto>> = snapshotFlow { search }
        .combine(_state) { q, s ->
            if (s is State.Success)
                s.products.filter {
                    it.name?.contains(q, ignoreCase = true) == true ||
                            it.brand?.contains(q, ignoreCase = true) == true
                }
            else emptyList()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = State.Loading
            when (val result = favoriteRepository.getSavedProducts()) {
                is FavoriteResult.Success -> _state.value = State.Success(result.data)
                is FavoriteResult.Error   -> _state.value = State.Error(result.message)
            }
        }
    }

    fun onSearchChange(q: String) { search = q }
}
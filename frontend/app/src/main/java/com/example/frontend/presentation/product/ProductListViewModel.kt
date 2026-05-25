package com.example.frontend.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.HomeSection
import com.example.frontend.data.model.ProductCardDto
import com.example.frontend.data.network.remote.ProductListApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProductListState {

    object Loading : ProductListState()

    data class Home(
        val sections: List<HomeSection>
    ) : ProductListState()

    data class Success(
        val products: List<ProductCardDto>,
        val total: Int
    ) : ProductListState()

    data class Error(
        val message: String
    ) : ProductListState()
}

@OptIn(FlowPreview::class)
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val api: ProductListApi,
    val productStore: ProductStore
) : ViewModel() {

    private val _state       = MutableStateFlow<ProductListState>(ProductListState.Loading)
    val state: StateFlow<ProductListState> = _state.asStateFlow()

    private val _search      = MutableStateFlow("")
    val search: StateFlow<String> = _search.asStateFlow()

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag: StateFlow<String?> = _selectedTag.asStateFlow()

    private val _tags        = MutableStateFlow<List<String>>(emptyList())
    val tags: StateFlow<List<String>> = _tags.asStateFlow()

    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType: StateFlow<String?> = _selectedType.asStateFlow()

    val productTypes = listOf(
        "Facial Treatment", "Oil", "Face Cleanser", "Sheet Mask", "Serum", "Lip Mask",
        "Lip Moisturizer", "Exfoliator", "General Moisturizer", "Night Moisturizer",
        "Wet Mask", "Overnight Mask", "Hand Care", "Essence", "Bath & Body", "Day Moisturizer",
        "Sunscreen", "Emulsion", "Eye Mask", "Toner", "Eye Moisturizer"
    )

    init {
        loadTags()

        combine(_search, _selectedTag) { search, tag ->
            Pair(search, tag)
        }
            .debounce(400)
            .onEach { (search, tag) ->

                if (search.isBlank() && tag == null) {
                    loadHome()
                } else {
                    loadProducts(search, tag)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadHome() {

        viewModelScope.launch {

            _state.value = ProductListState.Loading

            try {

                val acne = api.getProducts(
                    afterUse = "Acne Fighting",
                    limit = 10
                )

                val antiAging = api.getProducts(
                    afterUse = "Anti-Aging",
                    limit = 10
                )

                val cleansers = api.getProducts(
                    type = "Face Cleanser",
                    limit = 10
                )

                val moisturizers = api.getProducts(
                    type = "Serum",
                    limit = 10
                )

                val sections = buildList {

                    acne.body()?.let {
                        add(
                            HomeSection(
                                "Acne Fighting",
                                it.products
                            )
                        )
                    }

                    antiAging.body()?.let {
                        add(
                            HomeSection(
                                "Anti-aging",
                                it.products
                            )
                        )
                    }

                    cleansers.body()?.let {
                        add(
                            HomeSection(
                                "Cleansers",
                                it.products
                            )
                        )
                    }

                    moisturizers.body()?.let {
                        add(
                            HomeSection(
                                "Moisturizers",
                                it.products
                            )
                        )
                    }
                }

                _state.value =
                    ProductListState.Home(sections)

            } catch (e: Exception) {

                _state.value =
                    ProductListState.Error(
                        e.message ?: "Error"
                    )
            }
        }
    }

    init {
        loadTags()

        combine(_search, _selectedTag, _selectedType) { search, tag, type ->
            Triple(search, tag, type)
        }
            .debounce(400)
            .onEach { (search, tag, type) ->
                if (search.isBlank() && tag == null && type == null) {
                    loadHome()
                } else {
                    loadProducts(search, tag, type)  // <-- type pasat aici
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadProducts(search: String?, afterUse: String?, type: String? = null) {
        viewModelScope.launch {
            _state.value = ProductListState.Loading
            try {
                val response = api.getProducts(
                    search   = search?.takeIf { it.isNotBlank() },
                    afterUse = afterUse,
                    type     = type
                )
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    _state.value = ProductListState.Success(body.products, body.total)
                } else {
                    _state.value = ProductListState.Error("Error (${response.code()}).")
                }
            } catch (e: Exception) {
                _state.value = ProductListState.Error(e.message ?: "Unknown error")
            }
        }
    }


    private fun loadTags() {
        viewModelScope.launch {
            try {
                val response = api.getTags()
                if (response.isSuccessful) {
                    _tags.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) { /* tags sunt opționale */ }
        }
    }

    fun onSearchChange(query: String) {
        _search.value = query
    }

    fun onTagSelected(tag: String?) {
        _selectedTag.value  = if (_selectedTag.value == tag) null else tag
        _selectedType.value = null
    }

    fun onTypeSelected(type: String) {
        _selectedType.value = if (_selectedType.value == type) null else type
        _selectedTag.value = null
    }

    fun clearFilters() {
        _search.value      = ""
        _selectedTag.value = null
        _selectedType.value = null
    }
}
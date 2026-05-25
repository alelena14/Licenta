package com.example.frontend.domain.repository

import android.util.Log
import com.example.frontend.data.model.SaveFavoriteRequest
import com.example.frontend.data.model.SaveFavoriteResponse
import com.example.frontend.data.model.SavedProductDto
import com.example.frontend.data.network.remote.FavoriteApi
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

sealed class FavoriteResult<out T> {
    data class Success<T>(val data: T) : FavoriteResult<T>()
    data class Error(val message: String) : FavoriteResult<Nothing>()
}

@Singleton
class FavoriteRepository @Inject constructor(
    private val api: FavoriteApi,
    private val auth: FirebaseAuth
) {

    suspend fun saveFavorite(
        productId:   Long,
        score:       Double?      = null,
        concerns:    List<String> = emptyList(),
        explanation: String?      = null
    ): FavoriteResult<SaveFavoriteResponse> {
        val uid = auth.currentUser?.uid
            ?: return FavoriteResult.Error("User error.")
        return try {
            val response = api.saveFavorite(uid,
                SaveFavoriteRequest(
                    productId   = productId,
                    score       = score,
                    concerns    = concerns,
                    explanation = explanation
                )
            )
            if (response.isSuccessful && response.body() != null) {
                FavoriteResult.Success(response.body()!!)
            } else {
                when (val code = response.code()) {
                    409  -> FavoriteResult.Error("The product is already saved.")
                    401  -> FavoriteResult.Error("Expired session.")
                    else -> FavoriteResult.Error("Error ($code).")
                }
            }
        } catch (e: Exception) {
            FavoriteResult.Error("No internet connection.")
        }
    }

    suspend fun getSavedProducts(): FavoriteResult<List<SavedProductDto>> {
        val uid = auth.currentUser?.uid
            ?: return FavoriteResult.Error("User error.")
        return try {
            val response = api.getSavedProducts(uid)
            if (response.isSuccessful && response.body() != null) {
                FavoriteResult.Success(response.body()!!)
            } else {
                FavoriteResult.Error("Error (${response.code()}).")
            }
        } catch (e: Exception) {
            FavoriteResult.Error("No internet connection.")
        }
    }

    suspend fun isFavorite(productId: Long): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return try {
            val response = api.isFavorite(productId, uid)
            response.isSuccessful && response.body()?.get("isFavorite") == true
        } catch (e: Exception) {
            Log.e("FAVORITE", "isFavorite error", e)
            false
        }
    }

    suspend fun removeFavorite(productId: Long): FavoriteResult<Unit> {
        val uid = auth.currentUser?.uid
            ?: return FavoriteResult.Error("User error.")
        return try {
            val response = api.removeFavorite(productId, uid)
            if (response.isSuccessful) FavoriteResult.Success(Unit)
            else FavoriteResult.Error("Error (${response.code()}).")
        } catch (e: Exception) {
            Log.e("FAVORITE", "removeFavorite error", e)
            FavoriteResult.Error("No internet connection.")
        }
    }
}
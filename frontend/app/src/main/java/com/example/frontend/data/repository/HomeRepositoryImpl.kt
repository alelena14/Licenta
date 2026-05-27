package com.example.frontend.data.repository

import com.example.frontend.data.model.ProductRecommendation
import com.example.frontend.data.network.remote.HomeApi
import com.example.frontend.domain.repository.HomeRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val homeApi: HomeApi,
    private val auth: FirebaseAuth
) : HomeRepository {

    override suspend fun getRecommendations(): List<ProductRecommendation> {
        val token = "Bearer ${auth.currentUser?.getIdToken(true)?.await()?.token}"
        return homeApi.getHomeRecommendations(token)
    }
}
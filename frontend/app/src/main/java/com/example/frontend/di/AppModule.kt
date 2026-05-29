package com.example.frontend.di

import com.example.frontend.data.network.remote.ChatApi
import com.example.frontend.data.network.remote.FavoriteApi
import com.example.frontend.data.network.remote.HomeApi
import com.example.frontend.data.network.remote.ProductListApi
import com.example.frontend.data.network.remote.ProfileApi
import com.example.frontend.data.network.remote.RecommendationApi
import com.example.frontend.data.network.remote.SavedChatApi
import com.example.frontend.data.network.remote.UserApiService
import com.example.frontend.data.repository.HomeRepositoryImpl
import com.example.frontend.data.repository.ProfileRepositoryImpl
import com.example.frontend.data.repository.RecommendationRepositoryImpl
import com.example.frontend.data.repository.UserRepositoryImpl
import com.example.frontend.domain.repository.HomeRepository
import com.example.frontend.domain.repository.ProfileRepository
import com.example.frontend.domain.repository.RecommendationRepository
import com.example.frontend.domain.repository.UserRepository
import com.example.frontend.domain.usecase.GetRecommendationsUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    // private const val BASE_URL = "https://licenta-6ixc.onrender.com"
    private const val BASE_URL = "http://192.168.1.129:8080/"

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)

    @Provides
    @Singleton
    fun providesUserRepository(
        api: UserApiService,
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository {
        return UserRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideRecommendationApi(retrofit: Retrofit): RecommendationApi =
        retrofit.create(RecommendationApi::class.java)

    @Provides
    @Singleton
    fun provideChatApi(retrofit: Retrofit): ChatApi =
        retrofit.create(ChatApi::class.java)

    @Provides
    @Singleton
    fun provideRecommendationRepository(
        api: RecommendationApi
    ): RecommendationRepository {
        return RecommendationRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        api: ProfileApi,
        auth: FirebaseAuth
    ): ProfileRepository {
        return ProfileRepositoryImpl(api, auth)
    }

    @Provides
    @Singleton
    fun provideHomeRepository(
        api: HomeApi,
        auth: FirebaseAuth
    ): HomeRepository {
        return HomeRepositoryImpl(api, auth)
    }

    @Provides
    @Singleton
    fun provideGetRecommendationsUseCase(
        repository: RecommendationRepository
    ): GetRecommendationsUseCase {
        return GetRecommendationsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSavedChatApi(retrofit: Retrofit): SavedChatApi {
        return retrofit.create(SavedChatApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFavoriteApi(retrofit: Retrofit): FavoriteApi {
        return retrofit.create(FavoriteApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProductListApi(retrofit: Retrofit): ProductListApi {
        return retrofit.create(ProductListApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): ProfileApi {
        return retrofit.create(ProfileApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHomeApi(retrofit: Retrofit): HomeApi {
        return retrofit.create(HomeApi::class.java)
    }
}


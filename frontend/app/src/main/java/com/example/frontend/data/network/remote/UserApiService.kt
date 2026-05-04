package com.example.frontend.data.network.remote

import com.example.frontend.data.model.UserDto
import com.example.frontend.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {

    @POST("api/users/sync")
    suspend fun syncUser(@Body userDto: UserDto): Response<UserResponse>
}

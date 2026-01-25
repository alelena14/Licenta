package com.licenta.licenta_backend.model

import com.fasterxml.jackson.annotation.JsonProperty

data class UserResponse(
    val id: Long,
    val email: String,
    @JsonProperty("username") val username: String?,
    @JsonProperty("age") val age: Int?,
    val profileImageUrl: String?
)
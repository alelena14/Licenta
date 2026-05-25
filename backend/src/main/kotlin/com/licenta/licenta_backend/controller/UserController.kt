package com.licenta.licenta_backend.controller

import com.licenta.licenta_backend.dto.UserDto
import com.licenta.licenta_backend.model.User
import com.licenta.licenta_backend.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping("/sync")
    fun syncUser(@RequestBody userData: UserDto): ResponseEntity<User> {
        val user = userService.authenticate(
            firebaseToken = userData.token,
            username = userData.username,
            age = userData.age,
            profileImageUrl = userData.profileImageUrl
        )
        return ResponseEntity.ok(user)
    }
}




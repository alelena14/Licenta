package com.licenta.licenta_backend.service

import com.google.firebase.auth.FirebaseAuth
import com.licenta.licenta_backend.model.User
import com.licenta.licenta_backend.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun authenticate(firebaseToken: String, username: String?, age: Int?, profileImageUrl: String?): User {
        val decodedToken = FirebaseAuth.getInstance()
            .verifyIdToken(firebaseToken)

        val firebaseUid = decodedToken.uid
        val email = decodedToken.email
            ?: throw RuntimeException("Email missing from Firebase token")

        val existingUser = userRepository.findByFirebaseUid(firebaseUid)
        if (existingUser != null) {
            return existingUser
        }

        val newUser = User(
            firebaseUid = firebaseUid,
            email = email,
            username = username,
            age = age,
            profileImageUrl = profileImageUrl
        )

        return userRepository.save(newUser)
    }
}

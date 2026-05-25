package com.licenta.licenta_backend.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream
import java.io.FileInputStream

@Configuration
class FirebaseConfig {

    @PostConstruct
    fun init() {


        val firebaseJson = System.getenv("FIREBASE_CREDENTIALS_JSON")

        val credentials =
            if (firebaseJson != null) {
                GoogleCredentials.fromStream(
                    ByteArrayInputStream(firebaseJson.toByteArray())
                )
            } else {
                GoogleCredentials.fromStream(
                    FileInputStream("firebase-service-account.json")
                )
            }

        val options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build()

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
        }
    }
}
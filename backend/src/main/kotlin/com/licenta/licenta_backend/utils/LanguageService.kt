package com.licenta.licenta_backend.utils

import com.github.pemistahl.lingua.api.Language
import com.github.pemistahl.lingua.api.LanguageDetector
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder
import org.springframework.stereotype.Component

@Component
class LanguageService {

    private val detector: LanguageDetector =
        LanguageDetectorBuilder.fromLanguages(
            Language.ENGLISH,
            Language.ROMANIAN
        ).build()

    fun isEnglish(text: String): Boolean {

        if (text.isBlank()) return false

        return detector.detectLanguageOf(text) == Language.ENGLISH
    }
}
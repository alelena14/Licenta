package com.licenta.licenta_backend.dto


data class GeminiApiResponse(
    val candidates: List<Candidate>
)

data class Candidate(
    val content: GeminiContent
)

data class GeminiContent(
    val parts: List<GeminiTextPart>
)

data class GeminiTextPart(
    val text: String
)
package com.licenta.licenta_backend.service

import com.licenta.licenta_backend.dto.*
import com.licenta.licenta_backend.repository.ConcernRepository
import com.licenta.licenta_backend.repository.ProductRepository
import com.licenta.licenta_backend.utils.ConcernCompatibility
import com.licenta.licenta_backend.utils.ProductTypeCategory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

private data class RecommendationGroup(
    val area: String,
    val concerns: List<String>
)

@Service
class ChatService(
    private val aiService: AiService,
    private val recommendationService: RecommendationService,
    private val concernRepository: ConcernRepository,
    private val productRepository: ProductRepository
) {

    fun chat(
        request: ChatRequest,
        image: MultipartFile? = null
    ): ChatResponse {

        // ─────────────────────────────────────────────────────────
        // IMAGE FLOW
        // ─────────────────────────────────────────────────────────

        if (image != null && !image.isEmpty) {
            return handleImageMessage(request, image)
        }

        // ─────────────────────────────────────────────────────────
        // TEXT FLOW
        // ─────────────────────────────────────────────────────────

        val lastUserMessage = request.messages
            .lastOrNull { it.role == "user" }
            ?.content
            ?: return ChatResponse(
                reply = "I didn't receive a message."
            )

        val intent = aiService.detectIntent(lastUserMessage)

        when (intent.type) {

            IntentType.CASUAL -> {
                return ChatResponse(
                    reply = generateCasualReply(lastUserMessage)
                )
            }

            IntentType.BODY_CARE -> {
                return ChatResponse(
                    reply = """
                        SkinAI currently focuses on facial skincare recommendations only.
                        I can help with acne, redness, dryness, wrinkles, pigmentation,
                        dark circles, and other face or eye concerns.
                    """.trimIndent()
                )
            }

            IntentType.INGREDIENT_QUESTION -> {
                return ChatResponse(
                    reply = generateIngredientAnswer(intent)
                )
            }

            IntentType.PRODUCT_QUESTION -> {
                return ChatResponse(
                    reply = generateProductAnswer(intent)
                )
            }

            IntentType.UNKNOWN -> {
                return ChatResponse(
                    reply = handleGeneralMessage(
                        lastUserMessage,
                        request.messages
                    )
                )
            }

            IntentType.RECOMMENDATION -> {
                return handleRecommendationIntent(
                    request,
                    intent
                )
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    // IMAGE HANDLER
    // ─────────────────────────────────────────────────────────

    private fun handleImageMessage(
        request: ChatRequest,
        image: MultipartFile
    ): ChatResponse {

        val (concerns, area) = aiService.extractConcernsFromFace(image)

        if (area == "other") {
            return ChatResponse(
                reply = """
                    SkinAI currently focuses on facial skincare only.
                    Please upload a clear image of the face or eye area.
                """.trimIndent()
            )
        }

        if (concerns.isEmpty()) {
            return ChatResponse(
                reply = """
                    I couldn't clearly detect skin concerns from the image.
                    Please upload a brighter, close-up photo focused strictly on the affected area.
                """.trimIndent()
            )
        }

        val intent = ChatIntent(
            type = IntentType.RECOMMENDATION,
            concerns = concerns,
            rawQuery = "Image skincare analysis"
        )

        return handleRecommendationIntent(
            request,
            intent,
            imageArea = area
        )
    }

    // ─────────────────────────────────────────────────────────
    // RECOMMENDATION FLOW
    // ─────────────────────────────────────────────────────────

    private fun handleRecommendationIntent(
        request: ChatRequest,
        intent: ChatIntent,
        imageArea: String? = null
    ): ChatResponse {

        val concernsByArea = when {

            imageArea != null -> {
                mapOf(imageArea to intent.concerns)
            }

            intent.concerns.isNotEmpty() -> {
                aiService.extractConcernsByArea(intent.rawQuery)
            }

            intent.isFollowUp -> {

                val previousUserMessages = request.messages
                    .filter { it.role == "user" }
                    .dropLast(1)
                    .map { it.content }

                val reconstructed = previousUserMessages
                    .joinToString("\n")

                aiService.extractConcernsByArea(reconstructed)
            }

            else -> {
                emptyMap()
            }
        }

        if (concernsByArea.isEmpty()) {
            return ChatResponse(
                reply = """
                    I couldn't identify specific skin concerns.
                    Could you describe them differently?
                """.trimIndent()
            )
        }

        if (concernsByArea.keys.any { it == "other" }) {
            return ChatResponse(
                reply = """
                    SkinAI currently focuses on facial skincare recommendations only.
                """.trimIndent()
            )
        }

        val requestedType = intent.productType
            ?.let { ProductTypeCategory.fromUserMessage(it) }

        val recommendationGroups = mutableListOf<RecommendationGroup>()

        concernsByArea.forEach { (area, concernCodes) ->

            ConcernCompatibility
                .groupCompatibleConcerns(concernCodes)
                .forEach { group ->

                    recommendationGroups.add(
                        RecommendationGroup(area, group)
                    )
                }
        }

        val maxPerGroup = when (recommendationGroups.size) {
            1 -> if (requestedType != null) 15 else 5
            2 -> if (requestedType != null) 8 else 3
            else -> if (requestedType != null) 5 else 2
        }

        data class ScoredProduct(
            val dto: ProductRecommendation,
            val area: String
        )

        val allScored = mutableListOf<ScoredProduct>()

        recommendationGroups.forEach { group ->

            val concernIds = concernRepository
                .findByCodeIn(group.concerns)
                .map { it.id }

            val recommended = recommendationService
                .recommendProducts(concernIds, group.area)

            recommended.take(maxPerGroup).forEach { rec ->

                allScored.add(
                    ScoredProduct(
                        dto = ProductRecommendation(
                            id = rec.product.id,
                            name = rec.product.name,
                            brand = rec.product.brand,
                            type = rec.product.type,
                            country = rec.product.country,
                            tags = productRepository
                                .findAfterUseLabelsByProductId(rec.product.id),
                            ingredients = productRepository
                                .findIngredients(rec.product.id),
                            score = rec.normalizedScore,
                            explanation = buildExplanation(rec),
                            warnings = rec.warnings,
                            url = rec.product.url
                        ),
                        area = group.area
                    )
                )
            }
        }

        val unique = allScored.distinctBy { it.dto.id }

        var typeNotFound = false

        val filtered = if (requestedType != null) {

            val faceFiltered = unique
                .filter { it.area == "face" }
                .filter {
                    ProductTypeCategory.fromDbType(it.dto.type) == requestedType
                }

            val eyeProducts = unique
                .filter { it.area == "eyes" }

            if (faceFiltered.isNotEmpty()) {
                (faceFiltered + eyeProducts)
                    .distinctBy { it.dto.id }
            } else {
                typeNotFound = unique.any { it.area == "face" }
                unique
            }

        } else {
            unique
        }

        val finalProducts = filtered
            .map { it.dto }
            .take(6)

        if (finalProducts.isEmpty()) {
            return ChatResponse(
                reply = """
                    I couldn't find suitable products for your concerns right now.
                """.trimIndent(),
                detectedConcerns = concernsByArea
                    .values
                    .flatten()
                    .distinct()
            )
        }

        val context = buildProductContext(
            recommendationGroups,
            concernsByArea,
            finalProducts
        )

        val reply = generateRecommendationResponse(
            userMessage = intent.rawQuery,
            history = request.messages.dropLast(1),
            context = context,
            productType = requestedType?.displayName,
            recommendationGroups = recommendationGroups,
            typeNotFound = typeNotFound
        )

        return ChatResponse(
            reply = reply,
            products = finalProducts,
            detectedConcerns = concernsByArea
                .values
                .flatten()
                .distinct()
        )
    }

    // ─────────────────────────────────────────────────────────
    // CASUAL
    // ─────────────────────────────────────────────────────────

    private fun generateCasualReply(
        message: String
    ): String {

        val requestBody = aiService.buildRequestBody(
            userPrompt = message,
            systemPrompt = """
                You are SkinAI, a friendly skincare assistant.
                Reply naturally in one short sentence.
            """.trimIndent(),
            maxTokens = 60
        )

        return aiService.callGroq(requestBody)
            ?: "You're welcome!"
    }

    // ─────────────────────────────────────────────────────────
    // INGREDIENT QUESTIONS
    // ─────────────────────────────────────────────────────────

    private fun generateIngredientAnswer(
        intent: ChatIntent
    ): String {

        val ingredient = intent.ingredient ?: "this ingredient"

        val requestBody = aiService.buildRequestBody(
            userPrompt = intent.rawQuery,
            systemPrompt = """
                You are SkinAI, a skincare ingredient expert.

                Explain what $ingredient does in skincare.

                Keep it concise:
                - 3-5 sentences
                - beginner friendly
                - mention benefits
                - mention irritation risks if relevant
            """.trimIndent(),
            maxTokens = 200
        )

        return aiService.callGroq(requestBody)
            ?: "$ingredient may help certain skin concerns."
    }

    // ─────────────────────────────────────────────────────────
    // PRODUCT QUESTIONS
    // ─────────────────────────────────────────────────────────

    private fun generateProductAnswer(
        intent: ChatIntent
    ): String {

        val requestBody = aiService.buildRequestBody(
            userPrompt = intent.rawQuery,
            systemPrompt = """
                You are SkinAI, a skincare assistant.

                Answer the user's product-related question naturally.
                Keep it concise and practical.
            """.trimIndent(),
            maxTokens = 200
        )

        return aiService.callGroq(requestBody)
            ?: "That product may work depending on your skin type and concerns."
    }

    // ─────────────────────────────────────────────────────────
    // GENERAL MESSAGE
    // ─────────────────────────────────────────────────────────

    private fun handleGeneralMessage(
        message: String,
        history: List<ChatMessage>
    ): String {

        val historyFormatted = history
            .takeLast(4)
            .filter { it.content.isNotBlank() }
            .joinToString("\n") {
                "${it.role}: ${it.content}"
            }

        val requestBody = aiService.buildRequestBody(
            userPrompt = if (historyFormatted.isNotBlank())
                "Conversation so far:\n$historyFormatted\n\nUser: $message"
            else message,

            systemPrompt = """
                You are SkinAI, a friendly skincare assistant.
                Help the user naturally.
                Keep responses concise.
            """.trimIndent(),

            maxTokens = 150
        )

        return aiService.callGroq(requestBody)
            ?: "How can I help with your skincare concerns?"
    }

    // ─────────────────────────────────────────────────────────
    // RESPONSE GENERATION
    // ─────────────────────────────────────────────────────────

    private fun generateRecommendationResponse(
        userMessage: String,
        history: List<ChatMessage>,
        context: String,
        productType: String?,
        recommendationGroups: List<RecommendationGroup>,
        typeNotFound: Boolean = false
    ): String {

        val historyFormatted = history
            .takeLast(4)
            .filter { it.content.isNotBlank() }
            .joinToString("\n") {
                "${it.role}: ${it.content}"
            }

        val groupNote =
            if (recommendationGroups.size > 1)
                "Some products target different concerns. Mention this naturally without creating explicit groups."
            else ""

        val typeNote = when {
            typeNotFound ->
                "- The requested product type was not found."

            productType != null ->
                "- User specifically wants a $productType."

            else -> ""
        }

        val requestBody = aiService.buildRequestBody(
            userPrompt = if (historyFormatted.isNotBlank())
                "Previous conversation:\n$historyFormatted\n\nUser: $userMessage"
            else userMessage,

            systemPrompt = """
                You are SkinAI, a concise skincare assistant.

                $context

                Rules:
                - Mention each product once only
                - 3-5 sentences
                - Warm and natural tone
                - Explain why products fit the concerns
                $typeNote
                $groupNote
                - End with one short tip or question
            """.trimIndent(),

            maxTokens = 300
        )

        return aiService.callGroq(requestBody)
            ?: "I found some products that may help your concerns."
    }

    // ─────────────────────────────────────────────────────────
    // PRODUCT CONTEXT
    // ─────────────────────────────────────────────────────────

    private fun buildProductContext(
        groups: List<RecommendationGroup>,
        concernsByArea: Map<String, List<String>>,
        products: List<ProductRecommendation>
    ): String {

        val sb = StringBuilder()

        val areaDescription = concernsByArea.entries
            .joinToString("; ") { (area, codes) ->
                "$area: ${codes.joinToString(", ")}"
            }

        sb.appendLine("USER CONCERNS: $areaDescription")

        if (groups.size > 1) {

            sb.appendLine("GROUPS:")

            groups.forEachIndexed { i, g ->
                sb.appendLine(
                    "${i + 1}. [${g.area}] ${g.concerns.joinToString(", ")}"
                )
            }
        }

        sb.appendLine("\nPRODUCTS:")

        products.forEachIndexed { index, p ->

            sb.appendLine(
                "${index + 1}. ${p.brand} - ${p.name} " +
                        "(${p.type}, ${"%.0f".format(p.score * 100)}% match)"
            )

            if (p.warnings.isNotEmpty()) {
                sb.appendLine("Warning: ${p.warnings.first()}")
            }
        }

        return sb.toString()
    }

    // ─────────────────────────────────────────────────────────
    // PRODUCT EXPLANATION
    // ─────────────────────────────────────────────────────────

    private fun buildExplanation(
        rec: RecommendedProduct
    ): String {

        val parts = mutableListOf<String>()

        rec.concernBreakdown.forEach { concernScore ->

            val topIngredients = concernScore.topIngredients
                .filter { it.contribution > 0 }
                .take(2)

            if (topIngredients.isEmpty()) return@forEach

            val ingredientList = topIngredients.joinToString(", ") { ing ->

                val mechanismLabel = when (ing.mechanism) {
                    "TREATS" -> "treats"
                    "PREVENTS" -> "helps prevent"
                    "SUPPORTS" -> "supports"
                    else -> "helps with"
                }

                "${ing.ingredientName} ($mechanismLabel)"
            }

            parts.add(
                "For ${concernScore.concernName}: $ingredientList"
            )
        }

        return parts.joinToString(". ").trim()
    }
}
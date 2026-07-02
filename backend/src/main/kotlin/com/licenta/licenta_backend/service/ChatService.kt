package com.licenta.licenta_backend.service

import com.licenta.licenta_backend.dto.*
import com.licenta.licenta_backend.repository.ConcernRepository
import com.licenta.licenta_backend.repository.ProductRepository
import com.licenta.licenta_backend.utils.ConcernCompatibility
import com.licenta.licenta_backend.utils.LanguageService
import com.licenta.licenta_backend.utils.ProductTypeCategory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import com.fasterxml.jackson.module.kotlin.readValue
private data class RecommendationGroup(
    val area: String,
    val concerns: List<String>,
    val productTypes: List<String>
)

@Service
class ChatService(
    private val aiService: AiService,
    private val recommendationService: RecommendationService,
    private val concernRepository: ConcernRepository,
    private val productRepository: ProductRepository,
    private val languageService: LanguageService,
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

        val simpleReplies = setOf(
            "yes",
            "no",
            "yeah",
            "nope",
            "ok",
            "okay",
            "thanks",
            "thank you",
            "please"
        )

        if (lastUserMessage.trim().lowercase() !in simpleReplies &&
            !languageService.isEnglish(lastUserMessage)
        ) {
            return ChatResponse(
                reply = "SkinAI currently supports English only. Please ask your question in English."
            )
        }

        val intent = aiService.detectIntent(lastUserMessage)


        if (intent.isFollowUp) {
            return ChatResponse(
                reply = handleGeneralMessage(lastUserMessage, request.messages)
            )
        }

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

        val extraction = aiService.extractConcerns(intent.rawQuery)
        val extractedProductTypes = extraction.third

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
        val totalConcerns = concernsByArea.values.flatten().distinct().size

        if (totalConcerns > 6) {
            return ChatResponse(
                reply = """
            I noticed that your request includes a large number of skin concerns.
            
            It's uncommon for all of these concerns to be present at the same time. To provide recommendations that are as accurate and personalized as possible, please use the Skin Analysis feature or send a clear photo of the affected area.
            
            Once the analysis is complete, I'll recommend products based on the concerns that are actually detected.
            """.trimIndent()
            )
        }

        val hasRequestedTypes = extractedProductTypes.isNotEmpty()

        val recommendationGroups = mutableListOf<RecommendationGroup>()

        concernsByArea.forEach { (area, concernCodes) ->

            ConcernCompatibility
                .groupCompatibleConcerns(concernCodes)
                .forEach { group ->

                    recommendationGroups.add(
                        RecommendationGroup(
                            area = area,
                            concerns = group,
                            productTypes = extractedProductTypes
                        )
                    )
                }
        }

        val maxPerGroup = when (recommendationGroups.size) {
            1 -> if (hasRequestedTypes) 15 else 5
            2 -> if (hasRequestedTypes) 8 else 3
            else -> if (hasRequestedTypes) 5 else 2
        }

        data class ScoredProduct(
            val dto: ProductRecommendation,
            val area: String,
            val groupId: Int
        )

        val allScored = mutableListOf<ScoredProduct>()

        recommendationGroups.forEachIndexed { index, group ->

            val concernIds = concernRepository
                .findByCodeIn(group.concerns)
                .map { it.id }

            val recommended = recommendationService
                .recommendProducts(concernIds, group.area, group.productTypes)


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
                        area = group.area,
                        groupId = index
                    )
                )
            }
        }

        val groupedProducts = allScored
            .groupBy { it.groupId }
            .mapValues { (_, products) ->
                products.distinctBy { it.dto.id }
            }

        val maxPerGroupFinal = if (recommendationGroups.size == 1) 3 else 2

        val productsByGroup: Map<Int, List<ProductRecommendation>> =
            recommendationGroups.indices.associateWith { groupIndex ->
                groupedProducts[groupIndex]
                    ?.take(maxPerGroupFinal)
                    ?.map { it.dto }
                    ?: emptyList()
            }

        val finalProducts = productsByGroup.values.flatten()

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

        val replies = mutableListOf<String>()

        recommendationGroups.forEachIndexed { index, group ->

            val groupProducts = productsByGroup[index] ?: emptyList()

            if (groupProducts.isEmpty()) return@forEachIndexed

            val context = buildProductContext(
                groups = listOf(group),
                concernsByArea = mapOf(group.area to group.concerns),
                products = groupProducts
            )

            val scopedQuery = "Recommend and explain skincare products for the " +
                    "${group.area} area, specifically for: ${group.concerns.joinToString(", ")}."

            val groupReply = generateRecommendationResponse(
                userMessage = scopedQuery,
                history = request.messages.dropLast(1),
                context = context,
                productType = extractedProductTypes.firstOrNull(),
                recommendationGroups = listOf(group),
                productsForContext = groupProducts
            )

            replies.add("**For ${group.area} — ${group.concerns.joinToString(", ")}:**\n$groupReply")
        }

        val reply = replies.joinToString("\n\n")

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

            maxTokens = 250
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
        productsForContext: List<ProductRecommendation>
    ): String {

        val historyFormatted = history
            .takeLast(4)
            .filter { it.content.isNotBlank() }
            .joinToString("\n") {
                "${it.role}: ${it.content}"
            }

        val productTypeInfo = productType?.let {
            "\nRequested product type: $it"
        } ?: ""

        val requestBody = aiService.buildRequestBody(

            userPrompt = if (historyFormatted.isNotBlank())
                """
                    Previous conversation:
                    $historyFormatted
                
                    User: $userMessage$productTypeInfo
                    """.trimIndent()
            else userMessage,

            systemPrompt = """
            You are SkinAI.

            The recommendation algorithm has already selected the correct skincare products.

            $context

            Your task is ONLY to explain, for EACH numbered product above, why it is suitable.

            CRITICAL: You must NEVER write the product's brand or name yourself.
            Refer to products ONLY by their number (1, 2, 3...).
            Do NOT invent, guess, or paraphrase any brand or product name.

            Return JSON ONLY, no markdown, no explanation outside JSON:

            {
              "items": [
                { "index": 1, "text": "one short sentence explaining why this product helps, mentioning ingredients only if listed above" },
                { "index": 2, "text": "..." }
              ],
              "closing": "one short skincare tip or question, generic, no product names"
            }

            Rules:
            - One item per product number listed above, same order, none skipped, none added.
            - Do not mention any concern, ingredient, or product not listed above.
            - Keep each "text" under 30 words.
        """.trimIndent(),

            maxTokens = 500
        )

        val raw = aiService.callGroq(requestBody)
            ?: return "I found some products that may help your concerns."

        return try {
            val mapper = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper()
            val parsed: Map<String, Any> = mapper.readValue(raw)

            val items = (parsed["items"] as? List<*>)
                ?.filterIsInstance<Map<String, Any>>()
                ?.associateBy { (it["index"] as? Number)?.toInt() }
                ?: emptyMap()

            val closing = parsed["closing"] as? String ?: ""

            val products = recommendationGroups.first().let { group ->
                group // nu folosim direct, doar pentru claritate; produsele reale vin din afară
            }

            // reconstruim folosind produsele reale, NU ce a scris modelul
            val sb = StringBuilder()

            productsForContext.forEachIndexed { i, product ->
                val text = (items[i + 1]?.get("text") as? String)?.trim() ?: ""
                sb.appendLine("**${product.brand} - ${product.name}**")
                if (text.isNotBlank()) sb.appendLine(text)
                sb.appendLine()
            }

            if (closing.isNotBlank()) sb.appendLine(closing)

            sb.toString().trim()

        } catch (e: Exception) {
            println("generateRecommendationResponse parse error: ${e.message}")
            "I found some products that may help your concerns."
        }
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

        val group = groups.first()

        sb.appendLine("AREA: ${group.area}")
        sb.appendLine("CONCERNS: ${group.concerns.joinToString(", ")}")

        sb.appendLine()
        sb.appendLine("RECOMMENDED PRODUCTS:")

        products.forEachIndexed { index, p ->

            sb.appendLine(
                "${index + 1}. ${p.brand} - ${p.name} " +
                        "(${p.type}, ${"%.0f".format(p.score * 100)}% match)"
            )

            sb.appendLine("Reason: ${p.explanation}")

            if (p.warnings.isNotEmpty()) {
                sb.appendLine("Warning: ${p.warnings.first()}")
            }
        }

        sb.appendLine()

        return sb.toString()
    }

    // ─────────────────────────────────────────────────────────
    // PRODUCT EXPLANATION
    // ─────────────────────────────────────────────────────────

    fun buildExplanation(
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
package com.licenta.licenta_backend.runner

import com.licenta.licenta_backend.dto.ChatMessage
import com.licenta.licenta_backend.dto.ChatRequest
import com.licenta.licenta_backend.service.ChatService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

/**
 * Runner pentru testare chatbot direct in consola.
 * Se activeaza DOAR cu profilul "chat-console":
 *
 *   Intellij → Edit Configurations → Active profiles: chat-console
 *   sau
 *   mvn spring-boot:run -Dspring-boot.run.profiles=chat-console
 *   sau
 *   java -jar app.jar --spring.profiles.active=chat-console
 */
@Component
@ConditionalOnProperty(
    prefix = "app.chat-console",
    name = ["enabled"],
    havingValue = "true"
)
class ChatConsoleRunner(
    private val chatService: ChatService
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        println("""
        ╔══════════════════════════════════════╗
        ║        SkinAI Chatbot Console        ║
        ║  Type 'exit' or 'quit' to stop      ║
        ║  Type 'clear' to reset conversation  ║
        ╚══════════════════════════════════════╝
        """.trimIndent())

        val history = mutableListOf<ChatMessage>()

        while (true) {
            print("\nYou: ")
            val input = readlnOrNull()?.trim() ?: break

            when (input.lowercase()) {
                "exit", "quit" -> {
                    println("Goodbye! 👋")
                    break
                }
                "clear" -> {
                    history.clear()
                    println("─── Conversation cleared ───")
                    continue
                }
                "" -> continue
            }

            // Adauga mesajul userului in history
            history.add(ChatMessage(role = "user", content = input))

            // Apeleaza ChatService
            print("SkinAI: thinking...")
            try {
                val response = chatService.chat(
                    ChatRequest(messages = history.toList(), sessionId = "console")
                )

                // Sterge "thinking..."
                print("\r" + " ".repeat(20) + "\r")

                // Afiseaza raspunsul
                println("SkinAI: ${response.reply}")

                // Afiseaza produsele recomandate daca exista
                if (response.products.isNotEmpty()) {
                    println("\n  ┌─ Recommended Products ──────────────────────")
                    response.products.forEachIndexed { i, product ->
                        println("  │ ${i + 1}. ${product.brand} - ${product.name}")
                        println("  │    Score: ${"%.0f".format(product.score * 100)}% relevance")
                        if (product.explanation.isNotBlank()) {
                            // Truncheaza explicatia la 80 chars pentru consola
                            val shortExplanation = if (product.explanation.length > 80)
                                product.explanation.take(80) + "..."
                            else product.explanation
                            println("  │    ${shortExplanation}")
                        }
                        if (product.warnings.isNotEmpty()) {
                            println("  │    ⚠️  ${product.warnings.take(2).joinToString("; ")}")
                        }
                        if (i < response.products.size - 1) println("  │")
                    }
                    println("  └────────────────────────────────────────────")
                }

                // Afiseaza concern-urile detectate
                if (response.detectedConcerns.isNotEmpty()) {
                    println("\n  [Detected concerns: ${response.detectedConcerns.joinToString(", ")}]")
                }

                // Adauga raspunsul in history pentru contextul urmator
                history.add(ChatMessage(role = "assistant", content = response.reply))

                // Limiteaza history la ultimele 20 mesaje (10 perechi)
                if (history.size > 20) {
                    history.subList(0, history.size - 20).clear()
                }

            } catch (e: Exception) {
                print("\r" + " ".repeat(20) + "\r")
                println("SkinAI: Sorry, something went wrong: ${e.message}")
                println("  [Stack trace: ${e.cause?.message}]")
                // Scoate mesajul userului din history daca a esuat
                history.removeLastOrNull()
            }
        }
    }
}
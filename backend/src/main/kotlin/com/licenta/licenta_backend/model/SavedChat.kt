package com.licenta.licenta_backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "saved_chats")
data class SavedChat(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val title: String,                          // titlu ales de user sau generat automat

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(
        mappedBy = "savedChat",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @OrderBy("position ASC")
    val messages: MutableList<SavedChatMessage> = mutableListOf()
)


@Entity
@Table(name = "saved_chat_messages")
data class SavedChatMessage(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saved_chat_id", nullable = false)
    val savedChat: SavedChat,

    @Column(nullable = false)
    val role: String,                           // "user" sau "assistant"

    @Column(nullable = false, columnDefinition = "TEXT")
    val content: String,


    @Column(nullable = false)
    val position: Int,                          // ordinea mesajului in conversatie

    @Column(nullable = false)
    val timestamp: Long = System.currentTimeMillis()
)
package com.licenta.licenta_backend.model
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val brand: String,
    val name: String,

    @Column(name = "type")
    val type: String,

    val country: String? = null,
    @Column(name = "url")
    var url: String? = null,

    @CreationTimestamp
    val createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    val updatedAt: Instant = Instant.now(),

    // relatii
    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    val ingredients: MutableSet<ProductIngredient> = mutableSetOf(),

    @ManyToMany
    @JoinTable(
        name = "product_after_use",
        joinColumns = [JoinColumn(name = "product_id")],
        inverseJoinColumns = [JoinColumn(name = "after_use_id")]
    )
    val afterUse: MutableSet<AfterUse> = mutableSetOf(),

)

package com.licenta.licenta_backend.model
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "products")
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var brand: String,
    var name: String,

    @Column(name = "type")
    var type: String,

    var country: String? = null,
    @Column(name = "url")
    var url: String? = null,

    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now(),

    // relatii
    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    var ingredients: MutableSet<ProductIngredient> = mutableSetOf(),

    @ManyToMany
    @JoinTable(
        name = "product_after_use",
        joinColumns = [JoinColumn(name = "product_id")],
        inverseJoinColumns = [JoinColumn(name = "after_use_id")]
    )
    var afterUse: MutableSet<AfterUse> = mutableSetOf(),

)

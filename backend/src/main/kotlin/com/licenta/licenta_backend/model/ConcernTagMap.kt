package com.licenta.licenta_backend.model

import jakarta.persistence.*

@Entity
@Table(name = "concern_tag_map")
data class ConcernTagMap(

    @EmbeddedId
    val id: ConcernTagMapId,

    @Column(name = "weight")
    val weight: Int
)

@Embeddable
data class ConcernTagMapId(

    @Column(name = "concern_id")
    val concernId: Long,

    @Column(name = "tag_id")
    val tagId: Long
)
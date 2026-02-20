package com.licenta.licenta_backend.dto
import com.fasterxml.jackson.annotation.JsonProperty

data class IngredientApiResponse(

    val name: String?,

    @JsonProperty("inci_name")
    val inciName: String?,

    val category: String?,

    val function: List<String>?,

    @JsonProperty("comedogenic_rating")
    val comedogenicRating: Int?,

    @JsonProperty("irritation_potential")
    val irritationPotential: String?,

    @JsonProperty("safety_score")
    val safetyScore: String?,

    val benefits: List<String>?,
    val concerns: List<String>?,

    @JsonProperty("suitable_for")
    val suitableFor: List<String>?,

    @JsonProperty("avoid_if")
    val avoidIf: List<String>?,

    @JsonProperty("pairs_well_with")
    val pairsWellWith: List<String>?,

    @JsonProperty("avoid_mixing")
    val avoidMixing: List<String>?,

    val description: String?,

    @JsonProperty("concentration_range")
    val concentrationRange: String?
)

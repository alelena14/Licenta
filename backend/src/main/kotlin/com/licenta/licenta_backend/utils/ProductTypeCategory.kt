package com.licenta.licenta_backend.utils

/**
 * Categorii simplificate de produse — pentru filtrare in chat si recomandari.
 * Mapeaza toate tipurile din DB la categorii mai largi.
 */
enum class ProductTypeCategory(val displayName: String) {
    SERUM("Serum"),
    MOISTURIZER("Moisturizer"),
    CLEANSER("Cleanser"),
    TONER("Toner"),
    SUNSCREEN("Sunscreen"),
    MASK("Mask"),
    EYE_CARE("Eye Care"),
    EXFOLIATOR("Exfoliator"),
    LIP_CARE("Lip Care"),
    OIL("Oil"),
    ESSENCE("Essence"),
    BODY_CARE("Body Care"),
    OTHER("Other");

    companion object {

        /**
         * Mapeaza tipul exact din DB la categoria simplificata.
         * Returneaza null daca nu gaseste match (trateaza ca "orice tip").
         */
        fun fromDbType(dbType: String?): ProductTypeCategory? {
            if (dbType == null) return null
            return when (dbType.trim().lowercase()) {
                "serum"                          -> SERUM
                "essence"                        -> ESSENCE
                "facial treatment"               -> SERUM        // tratamentele sunt de obicei serumuri
                "toner"                          -> TONER
                "emulsion"                       -> TONER        // emulsiile sunt similare tonerelor
                "general moisturizer",
                "day moisturizer",
                "night moisturizer"              -> MOISTURIZER
                "eye moisturizer"                -> EYE_CARE
                "eye mask"                       -> EYE_CARE
                "face cleanser"                  -> CLEANSER
                "exfoliator"                     -> EXFOLIATOR
                "sunscreen"                      -> SUNSCREEN
                "sheet mask",
                "wet mask",
                "overnight mask",
                "lip mask"                       -> MASK
                "lip moisturizer"                -> LIP_CARE
                "oil"                            -> OIL
                "hand care",
                "bath & body"                    -> BODY_CARE
                "other"                          -> OTHER
                else                             -> null
            }
        }

        /**
         * Detecteaza categoria din textul userului (pentru chat).
         */
        fun fromUserMessage(message: String): ProductTypeCategory? {
            val lower = message.lowercase()
            return when {
                lower.containsAny("serum") -> SERUM
                lower.containsAny("essence") -> ESSENCE
                lower.containsAny("moisturizer", "cream") -> MOISTURIZER
                lower.containsAny("cleanser", "facial wash", "face wash") -> CLEANSER
                lower.containsAny("toner") -> TONER
                lower.containsAny("sunscreen", "spf", "solar") -> SUNSCREEN
                lower.containsAny("mask") -> MASK
                lower.containsAny("eye cream", "eye care", "under eye") -> EYE_CARE
                lower.containsAny("exfoliant", "scrub", "acid toner", "peeling") -> EXFOLIATOR
                lower.containsAny("lip") -> LIP_CARE
                lower.containsAny("oil") -> OIL
                lower.containsAny("body", "hand") -> BODY_CARE
                else -> null
            }
        }

        private fun String.containsAny(vararg keywords: String) =
            keywords.any { this.contains(it) }
    }
}
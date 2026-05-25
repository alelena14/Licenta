package com.licenta.licenta_backend.utils

/**
 * Defineste compatibilitatea dintre concern-uri.
 *
 * COMPATIBIL = produsele care trateaza un concern tind sa functioneze bine si pentru celalalt.
 *   Ex: acne_inflammatory + oily_skin → produsele anti-acnee sunt de obicei si oil-control
 *
 * INCOMPATIBIL = concern-urile necesita abordari diferite sau chiar contradictorii.
 *   Ex: acne_inflammatory + sensitive_skin → tratamentele agresive pentru acnee irita pielea sensibila
 *   → recomandari separate
 *
 * Daca doua concern-uri nu sunt mentionate explicit → default COMPATIBIL (merg impreuna).
 */
object ConcernCompatibility {

    // ── Grupuri naturale ──────────────────────────────────────────────────────
    // Concern-urile din acelasi grup sunt intotdeauna compatibile intre ele.

    private val ACNE_GROUP = setOf(
        "acne_comedonal", "acne_inflammatory", "hormonal_acne",
        "blackheads", "whiteheads", "congested_skin", "sebaceous_filaments"
    )

    private val OILY_GROUP = setOf(
        "oily_skin", "combination_skin_oily_tzone", "enlarged_pores"
    )

    private val DRY_SENSITIVE_GROUP = setOf(
        "dry_skin", "dehydrated_skin", "flaky_skin", "damaged_barrier",
        "sensitive_skin", "irritated_skin", "eczema_prone", "rosacea_prone"
    )

    private val PIGMENTATION_GROUP = setOf(
        "hyperpigmentation", "melasma", "post_inflammatory_hyperpigmentation",
        "uneven_skin_tone", "dull_skin"
    )

    private val AGING_GROUP = setOf(
        "fine_lines", "wrinkles", "loss_of_firmness"
    )

    private val EYE_GROUP = setOf(
        "dark_circles", "under_eye_bags"
    )

    // ── Perechi explicit INCOMPATIBILE ────────────────────────────────────────
    // Daca userul are concern-uri din coloane diferite, acestea primesc grupuri separate.
    // Reprezentate ca Set<Set<String>> pentru ca relatia e simetrica.

    private val INCOMPATIBLE_PAIRS: Set<Set<String>> = setOf(
        // Acnee vs Piele sensibila/deteriorata — tratamentele pentru acnee (BHA, retinol, benzoyl)
        // pot fi prea agresive pentru pielea sensibila/eczema/rosacea
        setOf("acne_comedonal",    "sensitive_skin"),
        setOf("acne_comedonal",    "irritated_skin"),
        setOf("acne_comedonal",    "eczema_prone"),
        setOf("acne_comedonal",    "rosacea_prone"),
        setOf("acne_comedonal",    "damaged_barrier"),
        setOf("acne_inflammatory", "sensitive_skin"),
        setOf("acne_inflammatory", "irritated_skin"),
        setOf("acne_inflammatory", "eczema_prone"),
        setOf("acne_inflammatory", "rosacea_prone"),
        setOf("acne_inflammatory", "damaged_barrier"),
        setOf("hormonal_acne",     "sensitive_skin"),
        setOf("hormonal_acne",     "irritated_skin"),
        setOf("hormonal_acne",     "eczema_prone"),
        setOf("hormonal_acne",     "rosacea_prone"),
        setOf("hormonal_acne",     "damaged_barrier"),
        setOf("blackheads",        "sensitive_skin"),
        setOf("blackheads",        "eczema_prone"),
        setOf("blackheads",        "rosacea_prone"),
        setOf("blackheads",        "damaged_barrier"),
        setOf("whiteheads",        "sensitive_skin"),
        setOf("whiteheads",        "eczema_prone"),
        setOf("whiteheads",        "damaged_barrier"),
        setOf("congested_skin",    "sensitive_skin"),
        setOf("congested_skin",    "eczema_prone"),
        setOf("congested_skin",    "damaged_barrier"),

        // Ten gras vs Piele uscata/deshidratata — abordari opuse
        setOf("oily_skin",                    "dry_skin"),
        setOf("oily_skin",                    "flaky_skin"),
        setOf("combination_skin_oily_tzone",  "eczema_prone"),
        setOf("combination_skin_oily_tzone",  "damaged_barrier"),

        // Anti-aging puternic (retinol etc.) vs Piele sensibila/bariera deteriorata
        setOf("wrinkles",          "sensitive_skin"),
        setOf("wrinkles",          "irritated_skin"),
        setOf("wrinkles",          "eczema_prone"),
        setOf("wrinkles",          "rosacea_prone"),
        setOf("wrinkles",          "damaged_barrier"),
        setOf("loss_of_firmness",  "sensitive_skin"),
        setOf("loss_of_firmness",  "eczema_prone"),
        setOf("loss_of_firmness",  "damaged_barrier"),

        // Pigmentare (acizi exfolianti, vitamina C concentrata) vs Piele sensibila
        setOf("hyperpigmentation", "sensitive_skin"),
        setOf("hyperpigmentation", "irritated_skin"),
        setOf("hyperpigmentation", "eczema_prone"),
        setOf("hyperpigmentation", "rosacea_prone"),
        setOf("hyperpigmentation", "damaged_barrier"),
        setOf("melasma",           "sensitive_skin"),
        setOf("melasma",           "eczema_prone"),
        setOf("melasma",           "damaged_barrier"),
        setOf("post_inflammatory_hyperpigmentation", "sensitive_skin"),
        setOf("post_inflammatory_hyperpigmentation", "damaged_barrier")
    )

    // ── API public ────────────────────────────────────────────────────────────

    /**
     * Verifica daca doua concern-uri sunt incompatibile.
     */
    fun areIncompatible(a: String, b: String): Boolean =
        setOf(a, b) in INCOMPATIBLE_PAIRS

    /**
     * Primeste o lista de concern-uri si le imparte in grupuri compatibile.
     * Concern-urile incompatibile vor fi in grupuri diferite.
     *
     * Algoritm greedy: adauga fiecare concern in primul grup cu care e compatibil.
     * Daca nu e compatibil cu niciun grup existent, creeaza un grup nou.
     *
     * Returneaza lista de grupuri, fiecare grup = lista de concern codes.
     *
     * Exemplu:
     *   Input:  ["acne_inflammatory", "oily_skin", "sensitive_skin", "dry_skin"]
     *   Output: [["acne_inflammatory", "oily_skin"], ["sensitive_skin", "dry_skin"]]
     */
    fun groupCompatibleConcerns(concerns: List<String>): List<List<String>> {
        if (concerns.isEmpty()) return emptyList()
        if (concerns.size == 1) return listOf(concerns)

        val groups = mutableListOf<MutableList<String>>()

        for (concern in concerns) {
            // Cauta primul grup cu care concern-ul e compatibil (nu e incompatibil cu nimeni din grup)
            val targetGroup = groups.firstOrNull { group ->
                group.none { existingConcern -> areIncompatible(concern, existingConcern) }
            }

            if (targetGroup != null) {
                targetGroup.add(concern)
            } else {
                // Creeaza un grup nou
                groups.add(mutableListOf(concern))
            }
        }

        return groups
    }
}
package com.licenta.licenta_backend.service

import com.licenta.licenta_backend.model.*
import com.licenta.licenta_backend.repository.AfterUseRepository
import com.licenta.licenta_backend.repository.IngredientRepository
import com.licenta.licenta_backend.repository.ProductIngredientRepository
import com.licenta.licenta_backend.repository.ProductRepository
import com.licenta.licenta_backend.utils.Csv
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ImportService(
    private val productRepo: ProductRepository,
    private val ingredientRepo: IngredientRepository,
    private val productIngredientRepo: ProductIngredientRepository,
    private val afterUseRepo: AfterUseRepository
) {

    @Transactional
    fun importCsv(list: List<Csv>) {

        // 🔥 preload produse existente (FOARTE IMPORTANT)
        val existingProducts = productRepo.findAll()
            .associateBy {
                (it.name.trim().lowercase() + "|" + (it.brand?.trim()?.lowercase() ?: ""))
            }.toMutableMap()

        // 🔥 preload ingrediente existente
        val existingIngredients = ingredientRepo.findAll()
            .associateBy { it.name.trim().lowercase() }
            .toMutableMap()

        list.forEach { b ->

            val name = b.name.trim()
            val brand = b.brand.trim()
            val key = name.lowercase() + "|" + brand.lowercase()

            // ✅ produs existent sau nou
            val product = existingProducts[key] ?: run {
                val newProduct = productRepo.save(
                    Product(
                        brand = brand,
                        name = name,
                        type = b.type ?: "unknown",
                        country = b.country,
                        area = ""
                    )
                )
                existingProducts[key] = newProduct
                newProduct
            }

            // ─────────────────────────────
            // INGREDIENTE
            // ─────────────────────────────
            val ingredients = b.ingredientsRaw
                ?.removePrefix("[")
                ?.removeSuffix("]")
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                ?: emptyList()

            ingredients.forEach { ingNameRaw ->

                val ingName = ingNameRaw.trim()
                val ingKey = ingName.lowercase()

                val ingredient = existingIngredients[ingKey] ?: run {
                    val newIng = ingredientRepo.save(Ingredient(name = ingName))
                    existingIngredients[ingKey] = newIng
                    newIng
                }

                val piId = ProductIngredientId(
                    productId = product.id,
                    ingredientId = ingredient.id
                )

                // 🔥 evită duplicate în join
                if (!productIngredientRepo.existsById(piId)) {
                    val pi = ProductIngredient(
                        id = piId,
                        product = product,
                        ingredient = ingredient
                    )
                    productIngredientRepo.save(pi)
                }
            }

            // ─────────────────────────────
            // AFTER USE
            // ─────────────────────────────
            val afterUses = b.afterUseRaw
                ?.removePrefix("[")
                ?.removeSuffix("]")
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                ?: emptyList()

            afterUses.forEach { labelRaw ->

                val label = labelRaw.trim()

                val au = afterUseRepo.findByLabel(label)
                    ?: afterUseRepo.save(AfterUse(label = label))

                // 🔥 evită Lazy + duplicate
                if (!product.afterUse.any { it.label == au.label }) {
                    product.afterUse.add(au)
                }
            }

            productRepo.save(product)
        }

        println("DONE — imported ${list.size} products.")
    }
}
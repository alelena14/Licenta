package com.licenta.licenta_backend.service

import com.licenta.licenta_backend.model.*
import com.licenta.licenta_backend.repository.AfterUseRepository
import com.licenta.licenta_backend.repository.IngredientRepository
import com.licenta.licenta_backend.repository.ProductIngredientRepository
import com.licenta.licenta_backend.repository.ProductRepository
import com.licenta.licenta_backend.utils.Csv
import org.springframework.stereotype.Service

@Service
class ImportService(
    private val productRepo: ProductRepository,
    private val ingredientRepo: IngredientRepository,
    private val productIngredientRepo: ProductIngredientRepository,
    private val afterUseRepo: AfterUseRepository
) {

    fun importCsv(list: List<Csv>) {

        list.forEach { b ->

            // salvam produsul
            val product = productRepo.save(
                Product(
                    brand = b.brand,
                    name = b.name,
                    type = b.type ?: "unknown",
                    country = b.country,
                    area = ""
                )
            )

            // parse ingredients
            val ingredients = b.ingredientsRaw
                ?.removePrefix("[")
                ?.removeSuffix("]")
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                ?: emptyList()

            // pentru fiecare ingredient
            ingredients.forEachIndexed { idx, ingName ->

                // find or create ingredient
                val ingredient = ingredientRepo.findByName(ingName)
                    ?: ingredientRepo.save(Ingredient(name = ingName))

                // create join id
                val piId = ProductIngredientId(
                    productId = product.id,
                    ingredientId = ingredient.id
                )

                // create join entity
                val pi = ProductIngredient(
                    id = piId,
                    product = product,
                    ingredient = ingredient
                )

                productIngredientRepo.save(pi)
            }

            // parse afterUse
            val afterUses = b.afterUseRaw
                ?.removePrefix("[")
                ?.removeSuffix("]")
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                ?: emptyList()

            afterUses.forEach { label ->
                val au = afterUseRepo.findByLabel(label)
                    ?: afterUseRepo.save(AfterUse(label = label))
                product.afterUse.add(au)
            }

            productRepo.save(product)
        }

        println("DONE — imported ${list.size} products.")
    }
}

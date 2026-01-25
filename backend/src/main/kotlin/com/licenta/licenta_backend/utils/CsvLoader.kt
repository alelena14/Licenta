package com.licenta.licenta_backend.utils

import com.univocity.parsers.csv.CsvParser
import com.univocity.parsers.csv.CsvParserSettings

data class Csv(
    val brand: String,
    val name: String,
    val type: String?,
    val country: String?,
    val ingredientsRaw: String?,
    val afterUseRaw: String?
)

object CsvLoader {

    fun loadCsv(path: String): List<Csv> {
        val input = {}::class.java.classLoader.getResourceAsStream(path)
            ?: error("CSV B not found: $path")

        val settings = CsvParserSettings().apply {
            isHeaderExtractionEnabled = true
            format.quote = '"'
            maxCharsPerColumn = 200_000
        }

        val parser = CsvParser(settings)
        val rows = parser.parseAllRecords(input)

        return rows.map { r ->
            Csv(
                brand = r.getString("brand"),
                name = r.getString("name"),
                type = r.getString("type"),
                country = r.getString("country"),
                ingredientsRaw = r.getString("ingredients"),
                afterUseRaw = r.getString("afterUse")
            )
        }
    }
}

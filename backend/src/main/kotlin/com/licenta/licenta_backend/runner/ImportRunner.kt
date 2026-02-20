package com.licenta.licenta_backend.runner

import com.licenta.licenta_backend.service.ImportService
import com.licenta.licenta_backend.utils.CsvLoader
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = "app.import-runner",
    name = ["enabled"],
    havingValue = "true"
)
class ImportRunner(
    private val importService: ImportService
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val csv = CsvLoader.loadCsv("data/productsHalf.csv")
        importService.importCsv(csv)
    }
}

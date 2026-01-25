package com.licenta.licenta_backend

import com.licenta.licenta_backend.service.ImportService
import com.licenta.licenta_backend.utils.CsvLoader
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Runner(
    private val importService: ImportService
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        // Products + Ingredients import
        //val csv = CsvLoader.loadCsv("data/productsHalf.csv")
        //importService.importCsv(csv)
    }
}


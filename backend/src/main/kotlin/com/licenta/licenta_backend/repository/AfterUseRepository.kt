package com.licenta.licenta_backend.repository

import com.licenta.licenta_backend.model.AfterUse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface AfterUseRepository : JpaRepository<AfterUse, Long> {
    @Query("""
    SELECT DISTINCT a.label
    FROM AfterUse a
    ORDER BY a.label
""")
    fun findAllLabels(): List<String>

    fun findByLabel(label: String): AfterUse?
}
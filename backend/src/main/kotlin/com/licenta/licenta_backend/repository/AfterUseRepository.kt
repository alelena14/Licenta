package com.licenta.licenta_backend.repository

import com.licenta.licenta_backend.model.AfterUse
import org.springframework.data.jpa.repository.JpaRepository

interface AfterUseRepository : JpaRepository<AfterUse, Long> {
    fun findByLabel(label: String): AfterUse?
}
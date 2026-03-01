package com.licenta.licenta_backend.repository

import com.licenta.licenta_backend.model.ConcernTagMap
import com.licenta.licenta_backend.model.ConcernTagMapId
import org.springframework.data.jpa.repository.JpaRepository

interface ConcernTagMapRepository :
    JpaRepository<ConcernTagMap, ConcernTagMapId> {

    fun findByIdConcernIdIn(concernIds: List<Long>): List<ConcernTagMap>
}
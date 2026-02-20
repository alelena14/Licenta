package com.licenta.licenta_backend.repository


import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import com.licenta.licenta_backend.model.Concern

@Repository
interface ConcernRepository : JpaRepository<Concern, Long> {

    @Query("select c.code from Concern c")
    fun findAllCodes(): List<String>
}
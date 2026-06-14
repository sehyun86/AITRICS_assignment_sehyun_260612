package com.aitrics.assignment.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "patients")
class Patient(
    @Id
    @Column(name = "patient_id")
    val patientId: String,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var gender: String,

    @Column(name = "birth_date", nullable = false)
    var birthDate: LocalDate,

    @Version
    var version: Long = 0
)

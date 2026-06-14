package com.aitrics.assignment.domain

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(
    name = "vitals",
    uniqueConstraints = [
        UniqueConstraint(
            name = "idx_vital_composite",
            columnNames = ["patient_id", "recorded_at", "vital_type"]
        )
    ]
)
class Vital(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "patient_id", nullable = false)
    val patientId: String,

    @Column(name = "recorded_at", nullable = false)
    val recordedAt: OffsetDateTime,

    @Enumerated(EnumType.STRING)
    @Column(name = "vital_type", nullable = false)
    val vitalType: VitalType,

    @Column(nullable = false)
    var value: Double,

    @Version
    var version: Long = 1
)

package com.nearpick.app.domain.purchase.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "processed_event")
class ProcessedEventEntity(
    @Id
    @Column(name = "event_id", nullable = false, length = 36)
    val eventId: String,

    @Column(name = "processed_at", nullable = false)
    val processedAt: LocalDateTime = LocalDateTime.now()
)

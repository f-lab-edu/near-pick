package com.nearpick.app.domain.purchase.repository

import com.nearpick.app.domain.purchase.entity.ProcessedEventEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProcessedEventRepository : JpaRepository<ProcessedEventEntity, String>

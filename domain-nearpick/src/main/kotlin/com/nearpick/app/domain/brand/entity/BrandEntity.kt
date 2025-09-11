package com.nearpick.app.domain.brand.entity

import com.nearpick.app.domain.brand.dto.BrandResponse
import com.nearpick.app.domain.brand.dto.CreateBrandRequest
import com.nearpick.app.domain.brand.dto.GetBrandDetailResponse
import com.nearpick.app.domain.user.entity.UserEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "Brand")
@EntityListeners(AuditingEntityListener::class)
class BrandEntity(
    @Id
    @Column(name = "id", nullable = false, length = 255)
    val id: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    val ownerUserEntity: UserEntity,

    var name: String,

    var description: String? = null,

    val businessRegistrationNumber: String,

    var fullAddress: String,

    var addressDetail: String? = null,

    var province: String? = null,

    var district: String? = null,

    var neighborhood: String? = null,

    var street: String? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @CreatedBy
    var createdBy: String? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,

    @LastModifiedBy
    var updatedBy: String? = null
)

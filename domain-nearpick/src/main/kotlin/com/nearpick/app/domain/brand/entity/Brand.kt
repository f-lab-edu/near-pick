package com.nearpick.app.domain.brand.entity

import com.nearpick.app.domain.brand.dto.BrandResponse
import com.nearpick.app.domain.brand.dto.CreateBrandRequest
import com.nearpick.app.domain.brand.dto.GetBrandDetailResponse
import com.nearpick.app.domain.user.entity.User
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
class Brand(
    @Id
    @Column(name = "id", nullable = false, length = 255)
    val id: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    val ownerUser: User,

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
    val createdAt: LocalDateTime? = null,

    @CreatedBy
    val createdBy: String? = null,

    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,

    @LastModifiedBy
    val updatedBy: String? = null
) {
    companion object {
        fun createBySeller(request: CreateBrandRequest, user: User): Brand =
            Brand(
                id = UUID.randomUUID().toString(),
                ownerUser = user,
                name = request.name,
                description = request.description,
                businessRegistrationNumber = request.businessRegistrationNumber,
                fullAddress = request.fullAddress,
                addressDetail = request.addressDetail,
                province = request.province,
                district = request.district,
                neighborhood = request.neighborhood,
                street = request.street
            )

        fun toResponse(brand: Brand): BrandResponse =
            BrandResponse(
                id = brand.id,
                name = brand.name,
                description = brand.description,
                businessRegistrationNumber = brand.businessRegistrationNumber,
                fullAddress = brand.fullAddress,
                addressDetail = brand.addressDetail,
                province = brand.province,
                district = brand.district,
                neighborhood = brand.neighborhood,
                street = brand.street
            )

        fun toDetailResponse(brand: Brand): GetBrandDetailResponse =
            GetBrandDetailResponse(
                id = brand.id,
                ownerUser = User.toResponse(brand.ownerUser),
                name = brand.name,
                description = brand.description,
                businessRegistrationNumber = brand.businessRegistrationNumber,
                fullAddress = brand.fullAddress,
                addressDetail = brand.addressDetail,
                province = brand.province,
                district = brand.district,
                neighborhood = brand.neighborhood,
                street = brand.street
            )
    }
}

package com.nearpick.app.domain.brand.service

import com.nearpick.app.domain.brand.dto.BrandResponse
import com.nearpick.app.domain.brand.dto.CreateBrandRequest
import com.nearpick.app.domain.brand.dto.GetBrandDetailResponse
import com.nearpick.app.domain.brand.dto.UpdateBrandRequest


interface BrandService {
    fun createBrand(request: CreateBrandRequest, userId: String): BrandResponse
    fun findAllBrandByOwnerUser(userId: String): List<BrandResponse>
    fun findBrandDetail(id: String): GetBrandDetailResponse
    fun updateBrand(id: String, userId: String, request: UpdateBrandRequest): BrandResponse
    fun deleteBrand(id: String, userId: String)
}

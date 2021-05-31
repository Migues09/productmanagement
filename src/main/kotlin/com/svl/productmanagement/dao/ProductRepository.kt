package com.svl.productmanagement.dao

import com.svl.productmanagement.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.QueryByExampleExecutor
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long>, QueryByExampleExecutor<Product> {
    @Query("SELECT p FROM Product p WHERE p.productType = ?1")
    fun findByProductType(type: String) : List<Product>
}
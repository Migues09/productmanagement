package com.svl.productmanagement.dao

import com.svl.productmanagement.model.Product
import com.svl.productmanagement.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.QueryByExampleExecutor
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long>, QueryByExampleExecutor<Product> {

}
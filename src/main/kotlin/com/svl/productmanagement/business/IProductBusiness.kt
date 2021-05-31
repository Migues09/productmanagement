package com.svl.productmanagement.business

import com.svl.productmanagement.model.Product
import com.svl.productmanagement.model.User

interface IProductBusiness {

    fun listAllProducts() : List<Product>
    fun listProduct(id : Long) : Product
    fun saveProduct(product: Product) : Product
    fun findByProductType(type : String) : List<Product>?
}
package com.svl.productmanagement.business

import com.svl.productmanagement.model.Product

interface IProductBusiness {

    fun listAllProducts() : List<Product>
    fun listProduct(id : Long) : Product
    fun saveProduct(product: Product) : Product
    fun findByProductType(type : String) : List<Product>
}
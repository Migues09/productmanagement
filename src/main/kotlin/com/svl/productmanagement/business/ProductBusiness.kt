package com.svl.productmanagement.business

import com.svl.productmanagement.dao.ProductRepository
import com.svl.productmanagement.dao.UserRepository
import com.svl.productmanagement.exception.BusinessException
import com.svl.productmanagement.exception.NotFoundException
import com.svl.productmanagement.model.Product
import com.svl.productmanagement.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.Throws

@Service
class ProductBusiness : IProductBusiness {

    // Inject the repository into business
    @Autowired
    val productRepository : ProductRepository? = null

    @Throws(BusinessException::class)
    override fun listAllProducts(): List<Product> {
        try {
            return productRepository!!.findAll()
        }catch (e: Exception){
            throw BusinessException(e.message)
        }
    }

    @Throws(BusinessException::class, NotFoundException::class)
    override fun listProduct(id: Long): Product {
        val op : Optional<Product>
        try{
            op = productRepository!!.findById(id)
        } catch (e: Exception){
            throw BusinessException(e.message)
        }
        if(!op.isPresent){
            throw NotFoundException("The product with id $id is not found")
        }
        return op.get()
    }

    @Throws(BusinessException::class)
    override fun saveProduct(product: Product): Product {
        try {
            return productRepository!!.save(product)
        } catch (e: Exception){
            throw BusinessException(e.message)
        }
    }

    override fun findByProductType(type : String): List<Product> {
        return productRepository!!.findByProductType(type)
    }
}
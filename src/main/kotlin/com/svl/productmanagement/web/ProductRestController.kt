package com.svl.productmanagement.web

import com.svl.productmanagement.business.IProductBusiness
import com.svl.productmanagement.dtos.ProductDTO
import com.svl.productmanagement.exception.BusinessException
import com.svl.productmanagement.exception.NotFoundException
import com.svl.productmanagement.model.Product
import com.svl.productmanagement.utils.Constants
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping(Constants.URL_PRODUCT)
class ProductRestController {

    @Autowired
    val productBusiness : IProductBusiness? = null

    //Return list of all products
    @GetMapping("/all")
    fun listAllProducts(@CookieValue("jwt") jwt : String?) : ResponseEntity<Any> {
        return try {
            if(jwt == null){
                return ResponseEntity.status(401).body("unauthenticated")
            }

            val body = Jwts.parser().setSigningKey("secret").parseClaimsJws(jwt).body
            ResponseEntity(productBusiness!!.listAllProducts(), HttpStatus.OK)
        } catch (e: Exception){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    //Return a product by id
    @GetMapping("/{id}")
    fun listProduct(@CookieValue("jwt") jwt : String?, @PathVariable("id") id : Long) : ResponseEntity<Any>{
        try {
            if(jwt == null){
                return ResponseEntity.status(401).body("unauthenticated")
            }

            return ResponseEntity(productBusiness!!.listProduct(id), HttpStatus.OK)
        }catch (e: BusinessException){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } catch (e: NotFoundException){
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    //Save a product
    @PostMapping("/create")
    fun createProduct(@CookieValue("jwt") jwt : String?, @RequestBody product : Product): ResponseEntity<Any> {
        try {
            if(jwt == null){
                return ResponseEntity.status(401).body("unauthenticated")
            }
            productBusiness!!.saveProduct(product)
            return ResponseEntity.ok("Product created.")
        } catch (e: BusinessException){
            return ResponseEntity.status(500).body("Server Error")
        }
    }

    //Return a product by type
    @GetMapping("/type")
    fun listProduct(@CookieValue("jwt") jwt : String?, @RequestBody productDTO: ProductDTO) : ResponseEntity<Any>{
        try {
            if(jwt == null){
                return ResponseEntity.status(401).body("unauthenticated")
            }
            return ResponseEntity(productBusiness!!.findByProductType(productDTO.type), HttpStatus.OK)
        }catch (e: BusinessException){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } catch (e: NotFoundException){
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    //Update product
    @PutMapping("/{id}")
    fun updateProduct(@CookieValue("jwt") jwt : String?, @RequestBody product : Product, @PathVariable("id") id : Long): ResponseEntity<Any> {
        return try {
            if(jwt == null){
                return ResponseEntity.status(401).body("unauthenticated")
            }
            var auxProduct = productBusiness!!.listProduct(id)

            auxProduct.productName = product.productName
            auxProduct.productDesc = product.productDesc
            auxProduct.productType = product.productType
            auxProduct.productImg = product.productImg
            auxProduct.companyName = product.companyName
            productBusiness!!.saveProduct(auxProduct)

            ResponseEntity.ok("Product updated!!")
        } catch (e: BusinessException){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } catch (e:NotFoundException){
            ResponseEntity.status(404).body("Failed to update. Product not Found")
        }
    }


}
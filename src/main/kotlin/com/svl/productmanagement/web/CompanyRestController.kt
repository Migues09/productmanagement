package com.svl.productmanagement.web

import com.svl.productmanagement.business.ICompanyBusiness
import com.svl.productmanagement.exception.BusinessException
import com.svl.productmanagement.exception.NotFoundException
import com.svl.productmanagement.model.Company
import com.svl.productmanagement.utils.Constants
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping(Constants.URL_COMPANY)
class CompanyRestController {

    @Autowired
    val companyBusiness : ICompanyBusiness? = null

    //Return list of all business
    @GetMapping("/all")
    fun listAllCompanies(@CookieValue("jwt") jwt : String?) : ResponseEntity<Any> {
        return try {
            if(jwt == null){
                return ResponseEntity.status(401).body("unauthenticated")
            }

            val body = Jwts.parser().setSigningKey("secret").parseClaimsJws(jwt).body
            ResponseEntity(companyBusiness!!.listAllCompanies(), HttpStatus.OK)
        } catch (e: Exception){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    //Return a company by id
    @GetMapping("/{id}")
    fun listCompany(@CookieValue("jwt") jwt : String?, @PathVariable("id") id : Long) : ResponseEntity<Any>{
        try {
            if(jwt == null){
                return ResponseEntity.status(401).body("unauthenticated")
            }

            return ResponseEntity(companyBusiness!!.listCompany(id), HttpStatus.OK)
        }catch (e: BusinessException){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } catch (e: NotFoundException){
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    //Save a company
    @PostMapping("/create")
    fun createCompany(@CookieValue("jwt") jwt : String?, @RequestBody company : Company): ResponseEntity<Any> {
        try {
            if(jwt == null){
                return ResponseEntity.status(401).body("unauthenticated")
            }
            companyBusiness!!.saveCompany(company)
            return ResponseEntity.ok("Company created.")
        } catch (e: BusinessException){
            return ResponseEntity.status(500).body("Server Error")
        }
    }

    //Update company
    @PutMapping("/{id}")
    fun updateCompany(@CookieValue("jwt") jwt : String?, @RequestBody company : Company, @PathVariable("id") id : Long): ResponseEntity<Any> {
        return try {
            if(jwt == null){
                return ResponseEntity.status(401).body("unauthenticated")
            }
            var auxCompany = companyBusiness!!.listCompany(id)

            auxCompany.companyName = company.companyName
            auxCompany.city = company.city
            auxCompany.adress = company.adress
            auxCompany.phone = company.phone
            companyBusiness!!.saveCompany(auxCompany)

            ResponseEntity.ok("Company updated!!")
        } catch (e: BusinessException){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } catch (e:NotFoundException){
            ResponseEntity.status(404).body("Failed to update. Company not Found")
        }
    }
}
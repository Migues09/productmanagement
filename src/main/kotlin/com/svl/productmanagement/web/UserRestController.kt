package com.svl.productmanagement.web

import com.svl.productmanagement.business.IUserBusiness
import com.svl.productmanagement.exception.BusinessException
import com.svl.productmanagement.exception.NotFoundException
import com.svl.productmanagement.model.User
import com.svl.productmanagement.utils.Constants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// Return the mapped data
@RestController
@RequestMapping(Constants.URL_BASE_USER)
class UserRestController {

    @Autowired
    val userBusiness : IUserBusiness? = null

    //Return list of all users
    @GetMapping("")
    fun listAllUsers() : ResponseEntity<List<User>>{
        return try {
            ResponseEntity(userBusiness!!.listAllUsers(), HttpStatus.OK)
        } catch (e: Exception){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    //Return a user by id
    @GetMapping("/{id}")
    fun listUser(@PathVariable("id") id: Long) : ResponseEntity<User>{
        return try {
            ResponseEntity(userBusiness!!.listUser(id), HttpStatus.OK)
        } catch (e: BusinessException){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } catch (e: NotFoundException){
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    //Save an user
    @PostMapping("")
    fun saveUser(@RequestBody user : User): ResponseEntity<Any> {
        return try {
            userBusiness!!.saveUser(user)
            val responsHeader = HttpHeaders()
            responsHeader.set("location", Constants.URL_BASE_USER + "/"
            + user.id)
            ResponseEntity(responsHeader, HttpStatus.CREATED)
        } catch (e: BusinessException){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    //Update user
    @PutMapping("")
    fun updateUser(@RequestBody user : User): ResponseEntity<Any> {
        return try {
            userBusiness!!.saveUser(user)
            ResponseEntity(HttpStatus.OK)
        } catch (e: BusinessException){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    //Delete user
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable("id") id : Long) : ResponseEntity<Any>{
        return try {
            userBusiness!!.deleteUser(id)
            ResponseEntity(HttpStatus.OK)
        } catch (e: BusinessException){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } catch (e: NotFoundException){
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}